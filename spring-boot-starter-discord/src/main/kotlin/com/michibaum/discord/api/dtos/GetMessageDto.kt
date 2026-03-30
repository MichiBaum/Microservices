package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) for retrieving message details from Discord.
 *
 * @property id The unique identifier of the message. Can be null if unavailable.
 * @property content The content of the message. May be null if the message was deleted or content is unavailable.
 * @property channelId The unique identifier of the channel where the message was posted.
 * @property author The author of the message, represented by a `GetUserDto` object. Can be null if author information is unavailable.
 * @property reactions A list of reactions associated with the message, represented as `GetReactionDto`. May be null if no reactions exist.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetMessageDto(
    @JsonProperty("id") val id: String?, // TODO Discord IDs https://discord.com/developers/docs/reference#snowflakes
    @JsonProperty("content") val content: String?,
    @JsonProperty("channel_id") val channelId: String,
    @JsonProperty("author") val author: GetUserDto?,
    @JsonProperty("reactions") val reactions: List<GetReactionDto>?,
)
