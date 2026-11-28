package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.CreateMessageDto
import com.michibaum.discord.config.DiscordProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Collections
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class DiscordClientImplTest {

    private class MutableClock(initialMillis: Long = 10_000L) : Clock() {
        private val currentMillis = AtomicLong(initialMillis)

        fun advanceMillis(delta: Long) {
            if (delta > 0L) {
                currentMillis.addAndGet(delta)
            }
        }

        override fun getZone(): ZoneId = ZoneId.of("UTC")
        override fun withZone(zone: ZoneId): Clock = this
        override fun instant(): Instant = Instant.ofEpochMilli(currentMillis.get())
        override fun millis(): Long = currentMillis.get()
    }

    private fun okMessageResponse(
        id: String,
        channelId: String,
        content: String = "ok",
        extraHeaders: Map<String, String> = emptyMap(),
    ): MockClientHttpResponse {
        val json = """{"id":"$id","content":"$content","channel_id":"$channelId","author":null,"reactions":null}"""
        return MockClientHttpResponse(json.toByteArray(StandardCharsets.UTF_8), HttpStatus.OK).apply {
            headers.contentType = MediaType.APPLICATION_JSON
            extraHeaders.forEach { (k, v) -> headers.add(k, v) }
        }
    }

    @Test
    fun `throttles messages exceeding 5 per 5 seconds on the same channel`() {
        val clock = MutableClock(10_000L)
        val recordedSleeps = Collections.synchronizedList(mutableListOf<Long>())
        val requestTimestamps = mutableListOf<Long>()
        val counter = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { _, _, _ ->
            requestTimestamps.add(clock.millis())
            okMessageResponse(id = "${counter.incrementAndGet()}", channelId = "chan-1")
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild"),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms ->
                recordedSleeps.add(ms)
                clock.advanceMillis(ms)
            },
        )

        repeat(7) { index ->
            val result = client.sendMessage("chan-1", CreateMessageDto("msg-$index"))
            assertEquals("${index + 1}", result.id)
        }

        // First 5 messages go out immediately at t=10_000; 6th waits 5_000ms until t=15_000; 7th also goes out at t=15_000
        assertEquals(listOf(5_000L), recordedSleeps.toList())
        assertEquals(listOf(10_000L, 10_000L, 10_000L, 10_000L, 10_000L, 15_000L, 15_000L), requestTimestamps)
    }

    @Test
    fun `does not throttle independent channels against each other`() {
        val clock = MutableClock(10_000L)
        val recordedSleeps = Collections.synchronizedList(mutableListOf<Long>())
        val counter = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { request, _, _ ->
            val path = request.uri.path
            val channelId = path.substringAfter("/channels/").substringBefore("/messages")
            okMessageResponse(id = "${counter.incrementAndGet()}", channelId = channelId)
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild"),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms ->
                recordedSleeps.add(ms)
                clock.advanceMillis(ms)
            },
        )

        repeat(5) { client.sendMessage("chan-A", CreateMessageDto("a-$it")) }
        repeat(5) { client.sendMessage("chan-B", CreateMessageDto("b-$it")) }

        assertTrue(recordedSleeps.isEmpty())
    }

    @Test
    fun `respects X-RateLimit-Remaining zero and X-RateLimit-Reset-After header`() {
        val clock = MutableClock(20_000L)
        val recordedSleeps = mutableListOf<Long>()
        val callCount = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { _, _, _ ->
            val n = callCount.incrementAndGet()
            val extraHeaders = if (n == 1) {
                mapOf(
                    "X-RateLimit-Remaining" to "0",
                    "X-RateLimit-Reset-After" to "1.25",
                )
            } else {
                emptyMap()
            }
            okMessageResponse(id = "$n", channelId = "chan-1", extraHeaders = extraHeaders)
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild"),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms ->
                recordedSleeps.add(ms)
                clock.advanceMillis(ms)
            },
        )

        client.sendMessage("chan-1", CreateMessageDto("first"))
        client.sendMessage("chan-1", CreateMessageDto("second"))

        assertEquals(listOf(1_250L), recordedSleeps)
        assertEquals(2, callCount.get())
    }

    @Test
    fun `retries on HTTP 429 Too Many Requests using retry_after from Discord JSON payload`() {
        val clock = MutableClock(30_000L)
        val recordedSleeps = mutableListOf<Long>()
        val attempts = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { _, _, _ ->
            val attempt = attempts.incrementAndGet()
            if (attempt == 1) {
                val rateLimitJson = """{"message": "You are being rate limited.", "retry_after": 0.408, "global": false}"""
                MockClientHttpResponse(
                    rateLimitJson.toByteArray(StandardCharsets.UTF_8),
                    HttpStatus.TOO_MANY_REQUESTS,
                ).apply {
                    headers.contentType = MediaType.APPLICATION_JSON
                }
            } else {
                okMessageResponse(id = "recovered-1", channelId = "chan-1")
            }
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild", maxRetries = 3),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms ->
                recordedSleeps.add(ms)
                clock.advanceMillis(ms)
            },
        )

        val response = client.sendMessage("chan-1", CreateMessageDto("hello"))

        assertEquals("recovered-1", response.id)
        assertEquals(2, attempts.get())
        assertEquals(listOf(408L), recordedSleeps)
    }

    @Test
    fun `rethrows TooManyRequests when maxRetries is exhausted`() {
        val clock = MutableClock(40_000L)
        val recordedSleeps = mutableListOf<Long>()
        val attempts = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { _, _, _ ->
            attempts.incrementAndGet()
            val rateLimitJson = """{"message": "You are being rate limited.", "retry_after": 0.3, "global": false}"""
            MockClientHttpResponse(
                rateLimitJson.toByteArray(StandardCharsets.UTF_8),
                HttpStatus.TOO_MANY_REQUESTS,
            ).apply {
                headers.contentType = MediaType.APPLICATION_JSON
            }
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild", maxRetries = 2),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms ->
                recordedSleeps.add(ms)
                clock.advanceMillis(ms)
            },
        )

        assertThrows(HttpClientErrorException.TooManyRequests::class.java) {
            client.sendMessage("chan-1", CreateMessageDto("fail"))
        }

        assertEquals(3, attempts.get())
        assertEquals(listOf(300L, 300L), recordedSleeps)
    }

    @Test
    fun `serializes concurrent virtual thread bursts without exceeding rate window`() {
        val clock = MutableClock(50_000L)
        val sentAt = Collections.synchronizedList(mutableListOf<Long>())
        val counter = AtomicInteger(0)

        val builder = RestClient.builder().requestInterceptor { _, _, _ ->
            sentAt.add(clock.millis())
            okMessageResponse(id = "${counter.incrementAndGet()}", channelId = "chan-burst")
        }

        val client = DiscordClientImpl(
            properties = DiscordProperties(enabled = true, botToken = "token", guildId = "guild"),
            restClientBuilder = builder,
            clock = clock,
            sleeper = { ms -> clock.advanceMillis(ms) },
        )

        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val tasks = (1..12).map { index ->
                Callable { client.sendMessage("chan-burst", CreateMessageDto("burst-$index")) }
            }
            val futures = executor.invokeAll(tasks)
            futures.forEach { it.get() }
        }

        assertEquals(12, sentAt.size)
        // Check that no 5-second window contains more than 5 messages
        for (i in 0 until sentAt.size) {
            val windowStart = sentAt[i]
            val inWindow = sentAt.count { ts -> ts >= windowStart && ts < windowStart + 5_000L }
            assertTrue(inWindow <= 5, "Expected at most 5 messages in 5s window starting at $windowStart, got $inWindow")
        }
    }
}
