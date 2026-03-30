package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing a user retrieved from the Discord API.
 *
 * @property id The unique identifier of the user. This follows the Discord snowflake format.
 * @property username The username of the user.
 * @property discriminator The discriminator assigned to the user's username.
 * @property globalName The optional global display name of the user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetUserDto(
    @JsonProperty("id") val id: String?, // TODO Discord IDs https://discord.com/developers/docs/reference#snowflakes
    @JsonProperty("username") val username: String,
    @JsonProperty("discriminator") val discriminator: String,
    @JsonProperty("global_name") val globalName: String?,
)
