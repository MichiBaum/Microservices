package com.michibaum.discord.api

import com.michibaum.discord.config.DiscordLoggingProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

/**
 * Polls a specified Discord channel for new messages and dispatches them to available `DiscordCommand` beans.
 *
 * This component runs on a fixed schedule and uses the Discord API to fetch the latest messages.
 * It filters out messages that the bot has already reacted to with the designated success emoji.
 * The remaining messages are evaluated against a list of commands, and if one or more commands
 * successfully process the message, the success emoji reaction is added.
 */
class DiscordMessagePoller(
    private val discordClient: DiscordClient,
    private val loggingProperties: DiscordLoggingProperties,
    private val commands: List<DiscordCommand>
) {

    private val logger = LoggerFactory.getLogger(DiscordMessagePoller::class.java)
    
    private val pollInterval = 5000L
    private val successEmoji = "✅"

    @Scheduled(fixedDelay = 5000L)
    fun pollMessages() {
        if (!loggingProperties.enabled || loggingProperties.channelId.isBlank()) {
            return
        }
        
        try {
            val messages = discordClient.getMessages(loggingProperties.channelId, 50)
            
            for (message in messages) {
                // Check if the bot has already reacted with the success emoji
                val alreadyProcessed = message.reactions?.any { reaction ->
                    reaction.me && reaction.emoji.name == successEmoji
                } ?: false

                if (alreadyProcessed) {
                    continue
                }

                // Check which commands support this message
                val supportedCommands = commands.filter { it.supports(message) }
                
                if (supportedCommands.isEmpty()) {
                    continue
                }

                var allSuccessful = true
                for (command in supportedCommands) {
                    try {
                        command.execute(message)
                    } catch (e: Exception) {
                        logger.error("Error executing command ${command.javaClass.simpleName} for message ${message.id}", e)
                        allSuccessful = false
                    }
                }

                // Add reaction only if at least one command supported it and all supported executed successfully
                if (allSuccessful && message.id != null) {
                    try {
                        discordClient.addReaction(loggingProperties.channelId, message.id, successEmoji)
                    } catch (e: Exception) {
                        logger.error("Failed to add reaction to message ${message.id}", e)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to poll Discord messages", e)
        }
    }
}
