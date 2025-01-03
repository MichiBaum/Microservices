package com.michibaum.discord.logging

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.dtos.CreateMessageDto
import com.michibaum.discord.config.DiscordLoggingProperties
import org.springframework.boot.availability.AvailabilityChangeEvent
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.event.EventListener


/**
 * A listener that triggers an action when the application has started.
 *
 * This class implements the ApplicationListener interface for the ApplicationStartedEvent.
 * When the application starts, it sends a message to a specified Discord channel indicating that the application has started.
 *
 * @property discordClient The client used to interact with the Discord API.
 * @property discordLoggingProperties The properties used to configure logging to Discord, including the channel ID where messages should be sent.
 */
class ApplicationStartedListener(private val discordClient: DiscordClient, private val discordLoggingProperties: DiscordLoggingProperties): ApplicationListener<ApplicationStartedEvent> {
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val message = CreateMessageDto("Application started, took ${event.timeTaken.toMillis()} ms")
        discordClient.sendMessage(discordLoggingProperties.channelId, message)
    }
}

/**
 * Listener that triggers when the application is ready.
 *
 * This class implements the ApplicationListener interface for the ApplicationReadyEvent
 * and sends a ready message to a specified Discord channel using the DiscordClient.
 *
 * @property discordClient the client used to interact with Discord API.
 * @property discordLoggingProperties properties that hold Discord logging configuration.
 */
class ApplicationReadyListener(private val discordClient: DiscordClient, private val discordLoggingProperties: DiscordLoggingProperties): ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val message = CreateMessageDto("Application ready, took ${event.timeTaken.toMillis()} ms")
        discordClient.sendMessage(discordLoggingProperties.channelId, message)
    }
}


/**
 * Listens for application availability events and sends a notification message to a specified Discord channel.
 *
 * @property discordClient The client used to communicate with the Discord API.
 * @property discordLoggingProperties The properties containing the Discord channel configuration.
 */
class ApplicationAvailabilityListener(private val discordClient: DiscordClient, private val discordLoggingProperties: DiscordLoggingProperties){

    /**
     * Handles the application availability change events and sends a message to a predefined
     * Discord channel indicating the application's new availability state.
     *
     * @param event The event containing the availability change details, including the new liveness state.
     */
    @EventListener
    fun onEvent(event: AvailabilityChangeEvent<LivenessState>) {
        val message = CreateMessageDto("Application availability ${event.state.name}")
        discordClient.sendMessage(discordLoggingProperties.channelId, message)
    }

}