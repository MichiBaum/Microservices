package com.michibaum.music_service.apis.spotify.oauth

data class SpotifyOAuthDto(
    val code: String,
    val grantType: String,
    val redirectUri: String
)