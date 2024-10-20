package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing the details of a Discord channel.
 *
 * @property id The unique identifier of the channel.
 * @property guildId The ID of the guild to which the channel belongs.
 * @property name The name of the channel.
 * @property type The type of the channel, represented using the `ChannelTypes` enum.
 * @property lastMessageId The ID of the last message sent in the channel, if any.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class GetChannelDto(
    @JsonProperty("id") val id: String,
    @JsonProperty("guild_id") val guildId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: ChannelTypes,
    @JsonProperty("last_message_id") val lastMessageId: String?,
)