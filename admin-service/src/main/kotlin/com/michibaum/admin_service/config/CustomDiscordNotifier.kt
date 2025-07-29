package com.michibaum.admin_service.config

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.dtos.CreateMessageDto
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.events.InstanceEvent
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier
import reactor.core.publisher.Mono

class CustomDiscordNotifier(private val discordClient: DiscordClient, private val adminDiscordProperties: AdminDiscordProperties, instanceRepository: InstanceRepository) : AbstractEventNotifier(instanceRepository) {

    override fun doNotify(event: InstanceEvent, instance: Instance): Mono<Void> {
        if(!adminDiscordProperties.enabled) {
            return Mono.empty()
        }

        return Mono.fromRunnable {
            val createMessageDto = CreateMessageDto(createContent(event, instance))
            discordClient.sendMessage(adminDiscordProperties.channelId, createMessageDto)
        }

    }

    protected fun createContent(event: InstanceEvent, instance: Instance?): String {
        return """
            Instance: ${event.instance.value}
            Version: ${event.version}
            Timestamp: ${event.timestamp}
            State: ${event.type}
        """.trimIndent()
    }

}