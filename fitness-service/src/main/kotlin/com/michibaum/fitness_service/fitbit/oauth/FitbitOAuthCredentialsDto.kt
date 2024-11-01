package com.michibaum.fitness_service.fitbit.oauth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class FitbitOAuthCredentialsDto(
    @JsonProperty("access_token")
    val accessToken: String, // The access token used to query the user's data
    @JsonProperty("expires_in")
    val expiresIn: String, //The lifespan of the access token in seconds
    @JsonProperty("refresh_token")
    val refreshToken: String, // The refresh token the application will use to obtain a new access and refresh token pair
    @JsonProperty("scope")
    val scope: String, // The scopes the user enabled on the Fitbit Authorization page
    @JsonProperty("user_id")
    val userId: String, // The user id of the person who authorized access
)