package com.michibaum.fitness_service.fitbit.oauth

data class FitbitRefreshBodyDto(
    val grantType: String,
    val refreshToken: String
)
