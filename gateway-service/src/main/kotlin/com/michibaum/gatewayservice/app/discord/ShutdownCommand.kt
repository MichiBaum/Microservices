package com.michibaum.gatewayservice.app.discord

import com.michibaum.discord.api.DiscordCommand
import com.michibaum.discord.api.dtos.GetMessageDto
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
import kotlin.system.exitProcess

@Component
class ShutdownCommand(
    private val context: ApplicationContext
) : DiscordCommand {

    override fun supports(message: GetMessageDto): Boolean {
        val content = message.content?.trim()
        return content == "/shutdown" || content == "/reboot"
    }

    override fun execute(message: GetMessageDto) {
        thread(start = true) {
            Thread.sleep(2000) // Wait to allow Discord client to add the success reaction
            val exitCode = SpringApplication.exit(context, { 0 })
            exitProcess(exitCode)
        }
    }
}
