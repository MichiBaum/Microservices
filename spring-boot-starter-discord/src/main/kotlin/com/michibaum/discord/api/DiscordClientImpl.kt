package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.*
import com.michibaum.discord.config.DiscordProperties
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.bodyWithType
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.ceil

/**
 * Implementation of the Discord client for making API calls to Discord services.
 *
 * The `DiscordClientImpl` class provides methods to interact with the Discord API, allowing for
 * operations such as retrieving guild information, fetching channel lists, creating channels,
 * and sending messages. It uses an HTTP client configured with necessary headers and base URL
 * specific to the Discord API v10, and enforces per-channel rate limiting and HTTP 429 retry handling.
 *
 * @property properties Configuration properties containing the bot token, guild ID, and retry settings.
 */
open class DiscordClientImpl internal constructor(
    private val properties: DiscordProperties,
    restClientBuilder: RestClient.Builder,
    private val clock: Clock,
    private val sleeper: (Long) -> Unit,
) : DiscordClient {

    constructor(properties: DiscordProperties) : this(
        properties = properties,
        restClientBuilder = RestClient.builder(),
        clock = Clock.systemUTC(),
        sleeper = { millis -> if (millis > 0L) Thread.sleep(millis) },
    )

    private val logger = LoggerFactory.getLogger(DiscordClientImpl::class.java)

    /**
     * The base URL for the Discord API v10.
     *
     * This URL serves as the entry point for making requests to the Discord API.
     * It includes the version number to specify the API version being used.
     *
     * @see [Discord API Documentation](https://discord.com/developers/docs/reference#api-versioning)
     */
    val url = "https://discord.com/api/v10/"

    /**
     * Represents the authorization token required for accessing the Discord API in this client.
     *
     * The token is prefixed with "Bot " as required by Discord's authentication scheme.
     * This token should be kept confidential and secure, avoiding exposure in version control or logs.
     *
     * @see [Discord API Documentation](https://discord.com/developers/docs/reference#authentication)
     */
    val authToken = "Bot ${properties.botToken}"

    /**
     * A pre-configured REST client used to interact with the Discord API.
     */
    private val httpClient: RestClient = restClientBuilder
        .baseUrl(url)
        .defaultHeaders { headers ->
            headers.add("Authorization", authToken)
            headers.add("Content-Type", "application/json")
            headers.add("User-Agent", "DiscordBot (https://github.com/MichiBaum/Microservices)")
        }.build()

    private val channelRateLimits = ConcurrentHashMap<String, ChannelRateLimitState>()
    private val globalRateLimitResetAtMillis = AtomicLong(0L)

    private class ChannelRateLimitState {
        val lock = ReentrantLock(true)
        val sentTimestampsMillis = ArrayDeque<Long>()
        val resetAtMillis = AtomicLong(0L)
    }

    /**
     * Retrieves detailed information about the guild associated with the Discord client.
     */
    override fun getGuild(): GetGuildeDto = executeWithRateLimitRetry {
        val body = httpClient.get() // https://docs.discord.com/developers/resources/guild#get-guild
            .uri("/guilds/${properties.guildId}")
            .retrieve()
            .body(object : ParameterizedTypeReference<GetGuildeDto>() {})
        requireNotNull(body) { "Discord API returned an empty response body for getGuild" }
    }

    /**
     * Retrieves a list of channels for the configured guild from the Discord API.
     */
    override fun getChannels(): List<GetChannelDto> = executeWithRateLimitRetry {
        val body = httpClient.get() // https://docs.discord.com/developers/resources/guild#get-guild-channels
            .uri("/guilds/${properties.guildId}/channels")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<GetChannelDto>>() {})
        requireNotNull(body) { "Discord API returned an empty response body for getChannels" }
    }

    /**
     * Retrieves details of a specific Discord channel.
     */
    override fun getChannel(channelId: String): GetChannelDto = executeWithRateLimitRetry {
        val body = httpClient.get() // https://docs.discord.com/developers/resources/channel#get-channel
            .uri("/channels/$channelId")
            .retrieve()
            .body(object : ParameterizedTypeReference<GetChannelDto>() {})
        requireNotNull(body) { "Discord API returned an empty response body for getChannel" }
    }

    /**
     * Creates a new Discord channel in the configured guild by making a POST request to the Discord API.
     */
    override fun createChannel(channelDto: CreateChannelDto): GetChannelDto = executeWithRateLimitRetry {
        val body = httpClient.post() // https://docs.discord.com/developers/resources/guild#create-guild-channel
            .uri("/guilds/${properties.guildId}/channels")
            .bodyWithType(channelDto)
            .retrieve()
            .body(object : ParameterizedTypeReference<GetChannelDto>() {})
        requireNotNull(body) { "Discord API returned an empty response body for createChannel" }
    }

    /**
     * Retrieves a list of messages from a specific Discord channel.
     */
    override fun getMessages(channelId: String, limit: Int): List<GetMessageDto> {
        require(limit in 1..100) { "limit must be between 1 and 100" }
        return executeWithRateLimitRetry {
            val body = httpClient.get() // https://docs.discord.com/developers/resources/message#get-channel-messages
                .uri { builder ->
                    builder.path("/channels/$channelId/messages")
                        .queryParam("limit", limit)
                        .build()
                }
                .retrieve()
                .body(object : ParameterizedTypeReference<List<GetMessageDto>>() {})
            requireNotNull(body) { "Discord API returned an empty response body for getMessages" }
        }
    }

    /**
     * Retrieves details of a specific Discord message.
     */
    override fun getMessage(channelId: String, messageId: String): GetMessageDto = executeWithRateLimitRetry {
        val body = httpClient.get() // https://docs.discord.com/developers/resources/message#get-channel-message
            .uri("/channels/$channelId/messages/$messageId")
            .retrieve()
            .body(object : ParameterizedTypeReference<GetMessageDto>() {})
        requireNotNull(body) { "Discord API returned an empty response body for getMessage" }
    }

    /**
     * Sends a message to a specified Discord channel, serializing and pacing requests per channel
     * to respect Discord's rate limits and retrying automatically on HTTP 429 responses.
     */
    override fun sendMessage(channelId: String, messageDto: CreateMessageDto): GetMessageDto {
        val state = channelRateLimits.computeIfAbsent(channelId) { ChannelRateLimitState() }
        return state.lock.withLock {
            executeWithRateLimitRetry {
                waitForChannelPermit(state)
                val response = httpClient.post() // https://docs.discord.com/developers/resources/message#create-message
                    .uri("/channels/$channelId/messages")
                    .bodyWithType(messageDto)
                    .retrieve()
                    .toEntity(object : ParameterizedTypeReference<GetMessageDto>() {})
                recordChannelResponse(state, response.headers)
                requireNotNull(response.body) { "Discord API returned an empty response body for sendMessage" }
            }
        }
    }

    /**
     * Adds a reaction to a specific Discord message.
     */
    override fun addReaction(channelId: String, messageId: String, emoji: String) { // https://docs.discord.com/developers/resources/message#create-reaction
        executeWithRateLimitRetry {
            httpClient.put()
                .uri("/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me", channelId, messageId, emoji)
                .retrieve()
                .toBodilessEntity()
        }
    }

    private fun <T> executeWithRateLimitRetry(block: () -> T): T {
        val maxRetries = properties.maxRetries.coerceAtLeast(0)
        var attempt = 0
        while (true) {
            waitUntilGlobalReset()
            try {
                return block()
            } catch (ex: HttpClientErrorException.TooManyRequests) {
                if (attempt >= maxRetries) {
                    throw ex
                }
                val (retryMillis, isGlobal) = parseRateLimitBackoff(ex)
                if (isGlobal) {
                    val resetAt = clock.millis() + retryMillis
                    globalRateLimitResetAtMillis.accumulateAndGet(resetAt, ::maxOf)
                }
                logger.debug(
                    "Discord rate limit hit (attempt {}/{}), backing off for {} ms (global={})",
                    attempt + 1,
                    maxRetries,
                    retryMillis,
                    isGlobal,
                )
                sleepMillis(retryMillis)
                attempt++
            }
        }
    }

    private fun waitUntilGlobalReset() {
        val waitMillis = globalRateLimitResetAtMillis.get() - clock.millis()
        if (waitMillis > 0L) {
            sleepMillis(waitMillis)
        }
    }

    private fun waitForChannelPermit(state: ChannelRateLimitState) {
        val resetWait = state.resetAtMillis.getAndSet(0L) - clock.millis()
        if (resetWait > 0L) {
            sleepMillis(resetWait)
        }

        val now = clock.millis()
        while (state.sentTimestampsMillis.isNotEmpty() && now - state.sentTimestampsMillis.first() >= CHANNEL_WINDOW_MILLIS) {
            state.sentTimestampsMillis.removeFirst()
        }

        if (state.sentTimestampsMillis.size >= MAX_MESSAGES_PER_CHANNEL_WINDOW) {
            val oldestTimestamp = state.sentTimestampsMillis.first()
            val windowWait = (oldestTimestamp + CHANNEL_WINDOW_MILLIS) - now
            if (windowWait > 0L) {
                sleepMillis(windowWait)
            }
            state.sentTimestampsMillis.removeFirst()
            val afterSleep = clock.millis()
            while (state.sentTimestampsMillis.isNotEmpty() && afterSleep - state.sentTimestampsMillis.first() >= CHANNEL_WINDOW_MILLIS) {
                state.sentTimestampsMillis.removeFirst()
            }
        }
    }

    private fun recordChannelResponse(state: ChannelRateLimitState, headers: HttpHeaders) {
        val now = clock.millis()
        state.sentTimestampsMillis.addLast(now)

        val remaining = headers.getFirst("X-RateLimit-Remaining")?.toIntOrNull()
        val resetAfterSeconds = headers.getFirst("X-RateLimit-Reset-After")?.toDoubleOrNull()
        if (remaining != null && remaining <= 0 && resetAfterSeconds != null && resetAfterSeconds > 0.0) {
            val resetMillis = ceil(resetAfterSeconds * 1000.0).toLong()
            state.resetAtMillis.accumulateAndGet(now + resetMillis, ::maxOf)
        }
    }

    private fun parseRateLimitBackoff(ex: HttpClientErrorException.TooManyRequests): Pair<Long, Boolean> {
        val body = runCatching { ex.getResponseBodyAs(RateLimitResponseDto::class.java) }.getOrNull()
        val headerSeconds = ex.responseHeaders?.getFirst("Retry-After")?.toDoubleOrNull()
            ?: ex.responseHeaders?.getFirst("X-RateLimit-Reset-After")?.toDoubleOrNull()
        val retryAfterSeconds = body?.retryAfter ?: headerSeconds
        val retryMillis = if (retryAfterSeconds != null && retryAfterSeconds > 0.0) {
            ceil(retryAfterSeconds * 1000.0).toLong().coerceAtLeast(MIN_RETRY_MILLIS)
        } else {
            DEFAULT_RETRY_MILLIS
        }
        val isGlobal = body?.global == true ||
            ex.responseHeaders?.getFirst("X-RateLimit-Global")?.equals("true", ignoreCase = true) == true
        return retryMillis to isGlobal
    }

    private fun sleepMillis(millis: Long) {
        if (millis <= 0L) return
        try {
            sleeper(millis)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException("Interrupted while waiting for Discord rate limit", ex)
        }
    }

    companion object {
        internal const val MAX_MESSAGES_PER_CHANNEL_WINDOW = 5
        internal const val CHANNEL_WINDOW_MILLIS = 5_000L
        internal const val MIN_RETRY_MILLIS = 50L
        internal const val DEFAULT_RETRY_MILLIS = 1_000L
    }
}