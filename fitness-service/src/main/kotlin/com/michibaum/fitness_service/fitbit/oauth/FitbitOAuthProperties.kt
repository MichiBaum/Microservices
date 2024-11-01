package com.michibaum.fitness_service.fitbit.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "fitbit.oauth")
data class FitbitOAuthProperties(
    var clientId: String = "",
    var clientSecret: String = "",
)