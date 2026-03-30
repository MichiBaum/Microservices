package com.michibaum.admin_service.config

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.DiscordMessageContentBuilder
import com.michibaum.discord.api.TimestampStyle
import com.michibaum.discord.api.dtos.CreateMessageDto
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.events.InstanceDeregisteredEvent
import de.codecentric.boot.admin.server.domain.events.InstanceEvent
import de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent
import de.codecentric.boot.admin.server.domain.events.InstanceRegistrationUpdatedEvent
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier
import reactor.core.publisher.Mono

class CustomDiscordNotifier(
    private val discordClient: DiscordClient,
    private val adminDiscordProperties: AdminDiscordProperties,
    instanceRepository: InstanceRepository
) : AbstractEventNotifier(instanceRepository) {

    override fun shouldNotify(event: InstanceEvent, instance: Instance): Boolean {
        if(!adminDiscordProperties.enabled) {
            return false
        }
        
        when(event){
            is InstanceStatusChangedEvent -> return true
            is InstanceRegisteredEvent -> return true
            is InstanceDeregisteredEvent -> return true
            is InstanceRegistrationUpdatedEvent -> return true
        }
        
        return false
    }

    override fun doNotify(event: InstanceEvent, instance: Instance): Mono<Void> =
        Mono.fromRunnable {
            val message = when(event){
                is InstanceStatusChangedEvent -> event.discordMessage(instance)
                is InstanceRegisteredEvent -> event.discordMessage(instance)
                is InstanceDeregisteredEvent -> event.discordMessage(instance)
                is InstanceRegistrationUpdatedEvent -> event.discordMessage(instance)
                else -> event.discordMessage(instance, {})
            }
            
            discordClient.sendMessage(adminDiscordProperties.channelId, message)
        }

}

fun InstanceEvent.discordMessage(
    instance: Instance,
    body: DiscordMessageContentBuilder.() -> Unit
): CreateMessageDto = CreateMessageDto.from {
    val appName = instance.registration.name
    header3("Application: $appName")
    newLine()

    bold("Timestamp: "); timestamp(timestamp, TimestampStyle.ISO_LIKE_DATE_TIME)
    newLine()
    
    bold("Type: "); append(type)
    newLine()

    body()
}

fun InstanceStatusChangedEvent.discordMessage(instance: Instance): CreateMessageDto = discordMessage(instance) {
    bold("Status changed: "); append(statusInfo.status)
    mapAsJson(statusInfo.details)
    newLine()
}

fun InstanceRegisteredEvent.discordMessage(instance: Instance): CreateMessageDto = discordMessage(instance) {
    bold("Status changed: "); append("Registered:")
    mapAsJson(registration.metadata)
    newLine()
}

fun InstanceDeregisteredEvent.discordMessage(instance: Instance): CreateMessageDto = discordMessage(instance) {
    bold("Status changed: "); append("Deregistered")
    newLine()
}

fun InstanceRegistrationUpdatedEvent.discordMessage(instance: Instance): CreateMessageDto = discordMessage(instance) {
    bold("Status changed: "); append("Registration updated:")
    mapAsJson(registration.metadata)
    newLine()
}