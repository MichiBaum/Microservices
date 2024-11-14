package com.michibaum.music_service.spotify.api.user.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyUserDto(
    @JsonProperty("display_name")
    val displayName: String,
    @JsonProperty("external_urls")
    val externalUrls: ExternalUrls,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Image>,
    val type: String,
    val uri: String,
)
