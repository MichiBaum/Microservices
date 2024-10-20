package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing guild information retrieved from the Discord API.
 *
 * @property id Unique identifier of the guild.
 * @property name Name of the guild.
 * @property description Optional description of the guild.
 * @property features List of features enabled for the guild.
 * @property ownerId Identifier of the user who owns the guild.
 * @property applicationId Optional application ID associated with the guild.
 * @property verificationLevel Verification level required for the guild.
 * @property roles List of roles available in the guild.
 * @property defaultMessageNotifications Default message notification settings for the guild.
 * @property maxMembers Maximum number of members allowed in the guild.
 * @property maxVideoChannelUsers Maximum number of users allowed in a video channel.
 * @property preferredLocale Preferred locale setting for the guild.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetGuildeDto  (
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("features") val features: List<String>,
    @JsonProperty("owner_id") val ownerId: String,
    @JsonProperty("application_id") val applicationId: Any?,
    @JsonProperty("verification_level") val verificationLevel: Long,
    @JsonProperty("roles") val roles: List<Role>,
    @JsonProperty("default_message_notifications") val defaultMessageNotifications: Long,
    @JsonProperty("max_members") val maxMembers: Long,
    @JsonProperty("max_video_channel_users") val maxVideoChannelUsers: Long,
    @JsonProperty("preferred_locale") val preferredLocale: String,
)

/**
 * Represents a role within a Discord guild.
 *
 * @property id Unique identifier of the role.
 * @property name Name of the role.
 * @property description Optional description of the role.
 * @property permissions Permissions assigned to the role in a bitwise value.
 * @property mentionable Whether the role can be mentioned by users.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Role(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("permissions") val permissions: String,
    @JsonProperty("mentionable") val mentionable: Boolean,
)