package com.michibaum.authentication_starter

import com.michibaum.authentication_library.public_endpoints.PublicEndpointDetails
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "public-endpoints")
data class PublicEndpointProperties(
    val packageNames: List<String>,
    val endpoints: List<PublicEndpointDetails> = emptyList()
)
