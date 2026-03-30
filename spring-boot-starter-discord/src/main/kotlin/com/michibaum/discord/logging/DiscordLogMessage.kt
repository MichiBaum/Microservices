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

    val discordMessage: CreateMessageDto = CreateMessageDto.from {
        header3(logEvent.level.toString())
        newLine()
        append(logEvent.formattedMessage)

        val throwableStr = ThrowableProxyUtil.asString(logEvent.throwableProxy)
        if (throwableStr.isNotBlank()) {
            newLine()
            codeBlock(throwableStr)
        }
    }

}