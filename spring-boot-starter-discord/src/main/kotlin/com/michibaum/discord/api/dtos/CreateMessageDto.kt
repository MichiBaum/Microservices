package com.michibaum.discord.api.dtos

/**
 * Data Transfer Object (DTO) for creating a message.
 *
 * @property content The content of the message to be sent.
 */
data class CreateMessageDto(
    val content: String,
)
