package com.michibaum.discord.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for enabling and setting up logging to a Discord channel.
 *
 * These properties provide the necessary configuration for connecting to Discord
 * and sending log messages to a specific channel.
 *
 * @property enabled Flag to determine if Discord logging is enabled.
 * @property channelId The ID of the Discord channel where log messages will be sent.
 */
@ConfigurationProperties(prefix = "spring.microservices.discord.logging")
data class DiscordLoggingProperties(
    var enabled: Boolean = false,
    var channelId: String = "",
)