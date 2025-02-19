package com.michibaum.authentication_starter

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "public-endpoints")
data class PublicEndpointProperties(
    val packageNames: List<String>
)
