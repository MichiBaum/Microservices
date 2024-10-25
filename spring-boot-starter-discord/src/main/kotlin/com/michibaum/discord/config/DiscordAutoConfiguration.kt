package com.michibaum.discord.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.DiscordClientImpl
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * Auto-configuration class for setting up Discord integration within a Spring application.
 *
 * This configuration class ensures that the necessary beans for interacting with the Discord API
 * are available in the application context, specifically the `DiscordClient`.
 */
@AutoConfiguration
@AutoConfigureAfter(value = [AutoConfigurationProperties::class])
@ConditionalOnBean(value = [DiscordProperties::class])
class DiscordAutoConfiguration {

    /**
     * Provides an `ObjectMapper` bean for JSON processing.
     *
     * This method returns a singleton instance of `ObjectMapper` which is used for JSON serialization and
     * deserialization throughout the application. The bean is only provided if an `ObjectMapper` bean
     * is not already present in the application context.
     *
     * @return An instance of `ObjectMapper`.
     */
    @Bean
    @ConditionalOnMissingBean
    fun objectMapper() = ObjectMapper()

    /**
     * Creates a `DiscordClient` bean if one is not already present in the application context.
     *
     * @param properties Configuration properties for the Discord integration.
     * @param objectMapper The ObjectMapper instance for JSON processing.
     * @return An instance of `DiscordClient`.
     */
    @Bean
    @ConditionalOnBean(value = [ObjectMapper::class])
    @ConditionalOnMissingBean
    fun discordClient(properties: DiscordProperties, objectMapper: ObjectMapper): DiscordClient {
        return DiscordClientImpl(properties, objectMapper)
    }

}