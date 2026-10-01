package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing an emoji from the Discord API.
 *
 * @property id The unique identifier of the emoji. Can be null for standard unicode emojis.
 * @property name The name of the emoji.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetEmojiDto(
    @JsonProperty("id") val id: String?,
    @JsonProperty("name") val name: String?,
)
