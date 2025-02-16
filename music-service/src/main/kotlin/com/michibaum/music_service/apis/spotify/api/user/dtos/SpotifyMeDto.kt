package com.michibaum.music_service.apis.spotify.api.user.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyMeDto (
    val country: String,
    @JsonProperty("display_name")
    val displayName: String,
    val email: String,
    @JsonProperty("explicit_content")
    val explicitContent: ExplicitContent,
    @JsonProperty("external_urls")
    val externalUrls: ExternalUrls,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Image>,
    val product: String,
    val type: String,
    val uri: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExplicitContent(
    @JsonProperty("filter_enabled")
    val filterEnabled: Boolean,
    @JsonProperty("filter_locked")
    val filterLocked: Boolean,
)