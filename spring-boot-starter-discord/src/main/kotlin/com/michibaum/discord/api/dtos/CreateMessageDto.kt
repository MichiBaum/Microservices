package com.michibaum.discord.api.dtos

import com.michibaum.discord.api.DiscordMessageContentBuilder

/**
 * Data Transfer Object (DTO) for creating a message.
 *
 * @property content The content of the message to be sent.
 */
data class CreateMessageDto(
    val content: String,
) {
    companion object {
        /**
         * Creates a `CreateMessageDto` using a fluent API for message content.
         *
         * @param contentBuilder A block that configures the message content using `DiscordMessageContentBuilder`.
         * @return A new `CreateMessageDto` with the built content.
         */
        fun from(contentBuilder: DiscordMessageContentBuilder.() -> Unit): CreateMessageDto =
            CreateMessageDto(DiscordMessageContentBuilder.build(contentBuilder))
    }
}
