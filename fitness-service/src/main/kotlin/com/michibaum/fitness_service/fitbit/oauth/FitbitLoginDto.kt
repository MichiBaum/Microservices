package com.michibaum.fitness_service.fitbit.oauth

data class FitbitLoginDto(
    val clientId: String,
    val codeChallenge: String,
    val state: String,
    val url: String,
)