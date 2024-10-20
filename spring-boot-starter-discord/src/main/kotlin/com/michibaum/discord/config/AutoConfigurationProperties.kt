package com.michibaum.discord.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

/**
 * Auto-configuration properties for setting up Discord integration within a Spring application.
 *
 * This class provides beans for configuring Discord integration and logging, based
 * on the availability of specific properties in the application configuration.
 */
@AutoConfiguration
class AutoConfigurationProperties {

    /**
     * Creates and provides a new instance of `DiscordProperties`, which holds
     * the configuration properties for the Discord integration. This bean is
     * conditionally created only if no other bean of the same type exists and
     * if the property `spring.microservices.discord.enabled` is set to `true`.
     *
     * @return a new `DiscordProperties` instance configured with the relevant
     *         properties for the Discord integration.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.microservices.discord", name = ["enabled"], havingValue = "true", matchIfMissing = false)
    fun discordProperties(): DiscordProperties = DiscordProperties()

    /**
     * Creates and provides a new instance of `DiscordLoggingProperties`, which holds
     * the configuration properties for logging integration with Discord. This bean is
     * conditionally created only if no other bean of the same type exists and if the property
     * `spring.microservices.discord.logging.enabled` is set to `true`.
     *
     * @return a new `DiscordLoggingProperties` instance configured with the relevant
     *         properties for logging integration with Discord.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.microservices.discord.logging", name = ["enabled"], havingValue = "true", matchIfMissing = false)
    fun discordLoggingProperties(): DiscordLoggingProperties = DiscordLoggingProperties()

}