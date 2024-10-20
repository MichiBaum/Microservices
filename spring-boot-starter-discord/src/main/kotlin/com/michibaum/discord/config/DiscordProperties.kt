package com.michibaum.discord.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for the Discord integration.
 *
 * This class binds to the properties defined with the prefix `spring.microservices.discord`.
 * It includes parameters to configure the Discord bot such as enabling the bot, setting the bot token,
 * and specifying the guild ID associated with the bot.
 *
 * @property enabled Indicates if the Discord integration is enabled.
 * @property botToken The token used to authenticate the Discord bot.
 * @property guildId The identifier of the Discord guild (server) the bot is associated with.
 */
@ConfigurationProperties(prefix = "spring.microservices.discord")
data class DiscordProperties (
    var enabled: Boolean = false,
    var botToken: String = "",
    var guildId: String = "",
){

}