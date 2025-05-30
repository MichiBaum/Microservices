package com.michibaum.discord.config

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
     * Creates a `DiscordClient` bean if one is not already present in the application context.
     *
     * @param properties Configuration properties for the Discord integration.
     * @return An instance of `DiscordClient`.
     */
    @Bean
    @ConditionalOnMissingBean
    fun discordClient(properties: DiscordProperties): DiscordClient {
        return DiscordClientImpl(properties)
    }

}