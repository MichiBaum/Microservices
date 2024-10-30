package com.michibaum.admin_service

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.dtos.CreateMessageDto
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.events.InstanceEvent
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier
import reactor.core.publisher.Mono

class CustomDiscordNotifier(val discordClient: DiscordClient, val adminDiscordProperties: AdminDiscordProperties, instanceRepository: InstanceRepository) : AbstractEventNotifier(instanceRepository) {

    override fun doNotify(event: InstanceEvent, instance: Instance): Mono<Void> {
        if(!adminDiscordProperties.enabled) {
            return Mono.empty()
        }

        return Mono.fromRunnable<Void> {
            val createMessageDto = CreateMessageDto(createContent(event, instance))
            discordClient.sendMessage(adminDiscordProperties.channelId, createMessageDto).block()
        }

    }

    protected fun createContent(event: InstanceEvent, instance: Instance?): String {
        return """
            
        """.trimIndent()
    }

}