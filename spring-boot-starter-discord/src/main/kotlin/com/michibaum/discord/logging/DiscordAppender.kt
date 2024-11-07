package com.michibaum.discord.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.config.DiscordLoggingProperties
import org.springframework.context.SmartLifecycle


/**
 * A logback appender that sends log events to a Discord channel.
 *
 * @property discordClient The client used to interface with the Discord API.
 * @property discordLoggingProperties The properties that configure how logging to Discord is performed.
 */
class DiscordAppender(private val discordClient: DiscordClient, private val discordLoggingProperties: DiscordLoggingProperties) : UnsynchronizedAppenderBase<ILoggingEvent>(), SmartLifecycle {

    /**
     * Appends a log event by sending a formatted message to a Discord channel.
     *
     * @param logEvent The log event that needs to be logged.
     */
    override fun append(logEvent: ILoggingEvent) {
        val message = DiscordLogMessage(logEvent).discordMessage
        try {
            discordClient.sendMessage(discordLoggingProperties.channelId, message)
                .block()
        } catch (_: Exception) { }
    }

    /**
     * Appends a log event by delegating to the `append` method.
     *
     * @param logEvent The log event that needs to be logged.
     */
    override fun doAppend(logEvent: ILoggingEvent) {
        append(logEvent)
    }

    /**
     * Indicates whether the DiscordAppender is currently running.
     *
     * @return true if the appender is started, false otherwise.
     */
    override fun isRunning(): Boolean {
        return isStarted
    }
}
