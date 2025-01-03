package com.michibaum.fitness_service.fitbit.oauth

data class FitbitTokenBodyDto(
    val clientId: String,
    val grantType: String,
    val code: String,
    val codeVerifier: String,
    val redirectUri: String
)
