package com.michibaum.music_service.spotify.oauth

data class SpotifyLoginDto(
    val clientId: String,
    val state: String,
    val url: String
)
