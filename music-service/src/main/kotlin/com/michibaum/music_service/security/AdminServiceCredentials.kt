package com.michibaum.music_service.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.boot.admin.client")
data class AdminServiceCredentials(
    var username: String = "",
    var password: String = ""
)