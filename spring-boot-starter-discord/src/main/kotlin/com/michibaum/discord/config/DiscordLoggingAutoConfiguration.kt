package com.michibaum.discord.config

import ch.qos.logback.classic.LoggerContext
import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.logging.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * Auto-configuration class for setting up logging to a Discord channel within a Spring application.
 *
 * This configuration class ensures the necessary beans for logging to a Discord channel
 * are available in the application context.
 */
@AutoConfiguration
@AutoConfigureAfter(value = [DiscordAutoConfiguration::class, AutoConfigurationProperties::class])
@ConditionalOnBean(value = [DiscordClient::class, DiscordLoggingProperties::class])
class DiscordLoggingAutoConfiguration {

    /**
     * Creates a `LoggerContext` bean for managing the logging configuration context.
     *
     * This method ensures that a `LoggerContext` instance is available in the Spring context,
     * providing the necessary configuration for logging within the application.
     *
     * @return A `LoggerContext` instance retrieved from the `LoggerFactory`.
     */
    @Bean
    @ConditionalOnClass(value = [LoggerFactory::class])
    @ConditionalOnMissingBean
    fun loggerContext(): LoggerContext =
        LoggerFactory.getILoggerFactory() as LoggerContext

    /**
     * Creates a `DiscordAppender` bean for logging events to a Discord channel.
     *
     * This method ensures that a `DiscordAppender` instance is available in the Spring context,
     * which can be used to send log messages to a specified Discord channel as defined
     * in the `DiscordLoggingProperties`.
     *
     * @param discordClient The client used to interact with the Discord API.
     * @param discordLoggingProperties The properties used to configure logging to Discord, including the channel ID where messages should be sent.
     * @return A `DiscordAppender` object initialized with the provided client and logging properties.
     */
    @Bean
    @ConditionalOnMissingBean
    fun discordAppender(discordClient: DiscordClient, discordLoggingProperties: DiscordLoggingProperties) =
        DiscordAppender(discordClient, discordLoggingProperties)

    /**
     * Configures and initializes the `DiscordAppender` within the `LoggerContext`.
     *
     * This method is triggered only if both `DiscordAppender` and `LoggerContext` beans are available in the Spring context.
     * It retrieves the names of the existing Discord appenders and configures them with the provided `DiscordAppender`.
     *
     * @param discordAppender The `DiscordAppender` instance used for logging to Discord.
     * @param loggerContext The `LoggerContext` instance managing the logging configuration and context.
     */
    @Bean
    @ConditionalOnBean(value = [DiscordAppender::class, LoggerContext::class])
    @ConditionalOnMissingBean
    fun configureAppender(discordAppender: DiscordAppender, loggerContext: LoggerContext) =
        ConfigureAppender(loggerContext, discordAppender).also {
            val appenderNames = it.getDiscordAppenderNames()
            it.configuredAppender(appenderNames)
        }

    /**
     * Creates an `ApplicationStartedListener` bean that listens for the application started event.
     *
     * When the application starts, this listener sends a notification message to a specified Discord channel
     * indicating the application has started.
     *
     * @param discordClient The client used to interact with the Discord API.
     * @param discordLoggingProperties The properties used to configure logging to Discord, including the channel ID where messages should be sent.
     * @return An `ApplicationStartedListener` object initialized with the provided client and logging properties.
     */
    @Bean
    @ConditionalOnMissingBean
    fun applicationStartedListener(discordClient: DiscordClient, discordLoggingProperties: DiscordLoggingProperties) =
        ApplicationStartedListener(discordClient, discordLoggingProperties)

    /**
     * Creates an `ApplicationReadyListener` bean which listens for the application ready event
     * and sends a notification message to a specified Discord channel.
     *
     * @param discordClient The client used to interact with the Discord API.
     * @param discordLoggingProperties The properties containing the Discord channel configuration.
     * @return An `ApplicationReadyListener` object initialized with the provided client and logging properties.
     */
    @Bean
    @ConditionalOnMissingBean
    fun applicationReadyListener(discordClient: DiscordClient, discordLoggingProperties: DiscordLoggingProperties) =
        ApplicationReadyListener(discordClient, discordLoggingProperties)

    /**
     * Creates an `ApplicationAvailabilityListener` bean which listens for application availability events
     * and sends a notification message to a specified Discord channel.
     *
     * @param discordClient The client used to interact with the Discord API.
     * @param discordLoggingProperties The properties containing the Discord channel configuration.
     * @return An `ApplicationAvailabilityListener` object initialized with the provided client and logging properties.
     */
    @Bean
    @ConditionalOnMissingBean
    fun applicationAvailabilityListener(discordClient: DiscordClient, discordLoggingProperties: DiscordLoggingProperties) =
        ApplicationAvailabilityListener(discordClient, discordLoggingProperties)

}
