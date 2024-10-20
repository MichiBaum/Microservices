package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) for retrieving message details from the Discord API.
 *
 * @property id The unique identifier of the message. It may be null if the message ID is unavailable.
 * @property content The content of the message. It may be null if the content is unavailable or was deleted.
 * @property channelId The unique identifier of the channel where the message was sent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetMessageDto(
    @JsonProperty("id") val id: String?, // TODO Discord IDs https://discord.com/developers/docs/reference#snowflakes
    @JsonProperty("content") val content: String?,
    @JsonProperty("channel_id") val channelId: String,
)
