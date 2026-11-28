package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.CircuitBreakerMetadata
import com.michibaum.permission_library.Permissions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatus
import java.net.URI

@ConfigurationProperties(prefix = "gateway.static-routes")
data class GatewayStaticRoutesProperties(
    val redirects: List<RedirectRouteProperties> = emptyList(),
    val externalServices: List<ExternalServiceRouteProperties> = emptyList()
)

data class RedirectRouteProperties(
    val hosts: List<String> = emptyList(),
    val status: HttpStatus = HttpStatus.TEMPORARY_REDIRECT,
    val targetUrl: URI? = null,
    val schemeHostTemplate: String? = null,
    val preservePathAndQuery: Boolean = false
)

data class ExternalServiceRouteProperties(
    val id: String,
    val hosts: List<String> = emptyList(),
    val paths: List<String> = emptyList(),
    val uri: URI,
    val order: Int = 0,
    val requiredPermissions: List<Permissions> = emptyList(),
    val circuitBreaker: CircuitBreakerMetadata? = null
)
