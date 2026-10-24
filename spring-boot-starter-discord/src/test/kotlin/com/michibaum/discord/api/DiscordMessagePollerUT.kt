package com.michibaum.discord.api

import com.michibaum.discord.config.DiscordLoggingProperties
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions

class DiscordMessagePollerUT {

    @Test
    fun `does not poll when there are no commands`() {
        val discordClient = mock(DiscordClient::class.java)
        val poller = DiscordMessagePoller(
            discordClient,
            DiscordLoggingProperties(enabled = true, channelId = "123"),
            emptyList()
        )

        poller.pollMessages()

        verifyNoInteractions(discordClient)
    }
}