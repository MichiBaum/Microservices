package com.michibaum.discord.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class DiscordLogMessageTest {

    @Test
    fun testLogMessageWithoutStacktrace() {
        val event = mock(ILoggingEvent::class.java)
        `when`(event.level).thenReturn(Level.INFO)
        `when`(event.formattedMessage).thenReturn("Test message")
        `when`(event.throwableProxy).thenReturn(null)

        val logMessage = DiscordLogMessage(event)

        assertEquals("### INFO\nTest message", logMessage.discordMessage.content)
    }

    @Test
    fun testLogMessageWithStacktrace() {
        val event = mock(ILoggingEvent::class.java)
        `when`(event.level).thenReturn(Level.ERROR)
        `when`(event.formattedMessage).thenReturn("Error message")

        val throwable = RuntimeException("Test exception")
        val throwableProxy = ThrowableProxy(throwable)
        `when`(event.throwableProxy).thenReturn(throwableProxy)

        val logMessage = DiscordLogMessage(event)

        val content = logMessage.discordMessage.content
        assertTrue(content.startsWith("### ERROR\nError message\n```"))
        assertTrue(content.contains("java.lang.RuntimeException: Test exception"))
        assertTrue(content.endsWith("\n```"))
    }
}
