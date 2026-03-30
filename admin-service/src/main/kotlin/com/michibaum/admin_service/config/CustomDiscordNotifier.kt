package com.michibaum.admin_service.config

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.dtos.CreateMessageDto
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.events.InstanceEvent
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
        
        if(event is InstanceStatusChangedEvent) {
            return true
        }
        
        return false
    }

    override fun doNotify(event: InstanceEvent, instance: Instance): Mono<Void> =
        Mono.fromRunnable {
            val createMessageDto = CreateMessageDto(createContent(event, instance))
            discordClient.sendMessage(adminDiscordProperties.channelId, createMessageDto)
        }

    protected fun createContent(event: InstanceEvent, instance: Instance): String {
        val appName = instance.registration.name

        return if (event is InstanceStatusChangedEvent) {
            """
                Application: $appName
                Status changed: ${event.statusInfo.status}
                Timestamp: ${event.timestamp}
            """.trimIndent()
        } else {
            """
                Application: $appName
                Event: ${event.type}
                Timestamp: ${event.timestamp}
            """.trimIndent()
        }
    }

}