package com.michibaum.discord.api

import com.michibaum.discord.api.dtos.GetMessageDto

/**
 * Interface for commands that can be executed based on Discord messages.
 * 
 * Implementations of this interface are intended to be Spring beans. The Discord starter
 * will automatically discover them and evaluate incoming unread messages against each command.
 */
interface DiscordCommand {
    /**
     * Checks if this command supports the given message.
     * 
     * @param message The message to evaluate.
     * @return `true` if this command supports processing the message, `false` otherwise.
     */
    fun supports(message: GetMessageDto): Boolean

    /**
     * Executes the command using the given message.
     * 
     * @param message The message to process.
     */
    fun execute(message: GetMessageDto)
}
