package com.michibaum.discord.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent

/**
 * Class responsible for configuring and managing Discord appenders within a logging context.
 *
 * @param loggerContext The LoggerContext instance managing the logging configuration and context.
 * @param discordAppender The DiscordAppender instance used for logging to Discord.
 */
class ConfigureAppender(
    private val loggerContext: LoggerContext,
    private val discordAppender: DiscordAppender,
) {

    /**
     * Retrieves the names of all Discord appenders present in the logger context.
     *
     * @return A list of names of the Discord appenders.
     */
    fun getDiscordAppenderNames(): List<String>{
        val listOfNames = ArrayList<String>()

        for (logger in loggerContext.loggerList) {
            val appenderIterator = logger.iteratorForAppenders()
            while (appenderIterator.hasNext()) {
                val appender = appenderIterator.next()
                if (appender is DiscordAppenderDelegator<ILoggingEvent>)
                    listOfNames.add(appender.name)

            }
        }

        return listOfNames
    }

    /**
     * Configures the log appenders with the given names in the current logger context
     * to use the DiscordAppender for logging events.
     *
     * @param names The list of appender names to be configured with the DiscordAppender.
     */
    fun configuredAppender(names: List<String>) {
        for (appenderName in names) {
            loggerContext.loggerList.stream()
                .filter { logger: Logger -> logger.getAppender(appenderName) != null }
                .forEach { logger: Logger ->
                    val bufferingLogGathererAppenderWrapper = logger.getAppender(appenderName) as DiscordAppenderDelegator<ILoggingEvent>
                    discordAppender.context = bufferingLogGathererAppenderWrapper.context
                    bufferingLogGathererAppenderWrapper.setDelegateAndReplayBuffer(discordAppender)
                }
        }

    }

}
