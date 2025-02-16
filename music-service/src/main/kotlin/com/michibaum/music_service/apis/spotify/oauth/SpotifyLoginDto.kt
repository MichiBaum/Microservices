package com.michibaum.music_service.apis.spotify.oauth

data class SpotifyLoginDto(
    val clientId: String,
    val state: String,
    val url: String
)
