package com.michibaum.music_service.spotify.oauth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyOAuthCredentialsDto(
    @JsonProperty("access_token")
    val accessToken: String, // An access token that can be provided in subsequent calls, for example to Spotify Web API services.
    @JsonProperty("token_type")
    val tokenType: String, // How the access token may be used: always "Bearer".
    @JsonProperty("scope")
    val scope: String, // A space-separated list of scopes which have been granted for this access_token
    @JsonProperty("expires_in")
    val expiresIn: String, // The time period (in seconds) for which the access token is valid.
    @JsonProperty("refresh_token")
    val refreshToken: String, //  A refresh token is a security credential that allows client applications to obtain new access tokens without requiring users to reauthorize the application.
)
