package com.michibaum.gatewayservice.app.discord

import com.michibaum.discord.api.dtos.GetMessageDto
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext

class ShutdownCommandUT {

    private val applicationContext = mockk<ApplicationContext>()
    private val shutdownCommand = ShutdownCommand(applicationContext)

    @Test
    fun `supports returns true for shutdown command`() {
        val message = GetMessageDto(
            id = "1",
            content = "/shutdown",
            channelId = "123",
            author = null,
            reactions = null
        )
        assertTrue(shutdownCommand.supports(message))
    }

    @Test
    fun `supports returns true for reboot command`() {
        val message = GetMessageDto(
            id = "1",
            content = "/reboot",
            channelId = "123",
            author = null,
            reactions = null
        )
        assertTrue(shutdownCommand.supports(message))
    }

    @Test
    fun `supports returns false for other commands`() {
        val message = GetMessageDto(
            id = "1",
            content = "/help",
            channelId = "123",
            author = null,
            reactions = null
        )
        assertFalse(shutdownCommand.supports(message))
    }

    @Test
    fun `supports returns false for null content`() {
        val message = GetMessageDto(
            id = "1",
            content = null,
            channelId = "123",
            author = null,
            reactions = null
        )
        assertFalse(shutdownCommand.supports(message))
    }
}
