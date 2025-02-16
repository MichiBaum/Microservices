package com.michibaum.music_service.apis.spotify.api.user.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExternalUrls(
    val spotify: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Followers(
    val href: String,
    val total: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(
    val url: String,
    val height: Long,
    val width: Long,
)