package com.michibaum.authentication_starter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.boot.admin.service")
data class AdminServiceCredentials(
    val username: String,
    val password: String
)