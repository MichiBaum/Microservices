package com.michibaum.music_service.spotify.oauth

data class SpotifyOAuthDto(
    val code: String,
    val grantType: String,
    val redirectUri: String
)