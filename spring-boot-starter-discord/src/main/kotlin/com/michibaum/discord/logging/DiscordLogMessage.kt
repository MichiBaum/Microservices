package com.michibaum.discord.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import com.michibaum.discord.api.dtos.CreateMessageDto

/**
 * Represents a log message formatted for Discord.
 *
 * @param logEvent The logging event containing details of the log.
 */
class DiscordLogMessage(logEvent: ILoggingEvent) {

    val discordMessage: CreateMessageDto

    init {
        val stacktrace = getStackTrace(logEvent)

        val message = """
            ### ${logEvent.level}
            ${logEvent.formattedMessage}
            $stacktrace
        """.trimIndent()
        this.discordMessage = CreateMessageDto(message)
    }

    /**
     * Extracts and formats the stack trace from a given logging event if available.
     *
     * @param logEvent The logging event containing details of the log and possibly a stack trace.
     * @return A formatted stack trace enclosed in code block quotes, or an empty string if no stack trace is available.
     */
    private fun getStackTrace(logEvent: ILoggingEvent): String {
        val throwableStr = ThrowableProxyUtil.asString(logEvent.throwableProxy)
        val messageStacktrace = if (throwableStr.isBlank()) {
            ""
        } else {
            """
                ```
                $throwableStr
                ```
                """.trimIndent()
        }
        return messageStacktrace
    }

}