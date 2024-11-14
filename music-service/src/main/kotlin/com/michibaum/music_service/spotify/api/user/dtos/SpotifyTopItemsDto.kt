package com.michibaum.music_service.spotify.api.user.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyTopItemsDto(
    val href: String,
    val limit: Long,
    val next: String,
    val offset: Long,
    val previous: String,
    val total: Long,
    val items: List<Item>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Item(
    @JsonProperty("external_urls")
    val externalUrls: ExternalUrls,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Long,
    val type: String,
    val uri: String,
)