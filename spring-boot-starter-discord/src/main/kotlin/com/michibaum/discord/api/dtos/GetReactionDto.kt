package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing a reaction to a Discord message.
 *
 * @property count The number of times this reaction has been used on the message.
 * @property me Indicates whether the current user has reacted with this emoji.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetReactionDto(
    @JsonProperty("count") val count: Int,
    @JsonProperty("me") val me: Boolean,
)
