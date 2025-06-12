package com.michibaum.gatewayservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "management.services")
data class ServicesProperties(
    val zipkinUrl: URI,
    val grafanaUrl: URI,
    val prometheusUrl: URI
)
