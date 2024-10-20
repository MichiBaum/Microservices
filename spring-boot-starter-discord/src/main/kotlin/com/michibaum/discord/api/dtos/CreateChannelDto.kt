package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing the details required to create a new Discord channel.
 *
 * @property name The name of the channel to be created.
 * @property type The type of the channel, represented as an integer. For valid types, refer to the `ChannelTypes` enum.
 * @property parentId Optional ID of the parent category under which the channel should be created. This is nullable.
 */
data class CreateChannelDto(
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: Int,
    @JsonProperty("parent_id") val parentId: String? = null,
)