package com.michibaum.admin_service.config

import com.michibaum.discord.api.DiscordClient
import com.michibaum.discord.api.dtos.CreateMessageDto
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent
import de.codecentric.boot.admin.server.domain.values.InstanceId
import de.codecentric.boot.admin.server.domain.values.Registration
import de.codecentric.boot.admin.server.domain.values.StatusInfo
import com.michibaum.discord.api.dtos.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import java.time.Instant

class CustomDiscordNotifierTest {

    @Test
    fun testDoNotify() {
        val discordClient = TestDiscordClient()
        val adminDiscordProperties = AdminDiscordProperties(enabled = true, channelId = "123")
        val instanceRepository = mock(InstanceRepository::class.java)

        val notifier = CustomDiscordNotifier(discordClient, adminDiscordProperties, instanceRepository)

        val instanceId = InstanceId.of("test")
        val instance = mock(Instance::class.java)
        val registration = Registration.create("TestApp", "http://localhost:8080/actuator/health").build()
        `when`(instance.registration).thenReturn(registration)

        val event = mock(InstanceStatusChangedEvent::class.java)
        `when`(event.instance).thenReturn(instanceId)
        val statusInfo = StatusInfo.ofUp()
        `when`(event.statusInfo).thenReturn(statusInfo)
        val timestamp = Instant.now()
        `when`(event.timestamp).thenReturn(timestamp)
        `when`(event.type).thenReturn("STATUS_CHANGED")

        `when`(instanceRepository.find(instanceId)).thenReturn(Mono.just(instance))

        notifier.notify(event).block()

        val (channelId, messageDto) = discordClient.sentMessages.first()
        val message = messageDto.content
        
        assert(channelId == "123")
        assertTrue(message.contains("### Application: TestApp"))
        assertTrue(message.contains("**Status changed: **UP"))
        assertTrue(message.contains("**Type: **STATUS_CHANGED"))
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(java.time.ZoneId.of("UTC"))
        val expectedTimestamp = formatter.format(timestamp)
        assertTrue(message.contains("**Timestamp: **$expectedTimestamp"))
    }

    class TestDiscordClient : DiscordClient {
        val sentMessages = mutableListOf<Pair<String, CreateMessageDto>>()

        override fun sendMessage(channelId: String, messageDto: CreateMessageDto): GetMessageDto {
            sentMessages.add(channelId to messageDto)
            return mock(GetMessageDto::class.java)
        }

        override fun getGuild(): GetGuildeDto = TODO()
        override fun getChannels(): List<GetChannelDto> = TODO()
        override fun createChannel(channelDto: CreateChannelDto): GetChannelDto = TODO()
        override fun getChannel(channelId: String): GetChannelDto = TODO()
        override fun getMessages(channelId: String, limit: Int): List<GetMessageDto> = TODO()
        override fun getMessage(channelId: String, messageId: String): GetMessageDto = TODO()
        override fun addReaction(channelId: String, messageId: String, emoji: String) = TODO()
    }
}
