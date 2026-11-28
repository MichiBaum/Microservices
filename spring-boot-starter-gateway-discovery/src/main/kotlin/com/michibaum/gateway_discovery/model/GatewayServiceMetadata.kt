package com.michibaum.gateway_discovery.model

import com.michibaum.permission_library.Permissions

object GatewayMetadataConstants {
    const val CURRENT_SCHEMA_VERSION: Int = 1
    const val KEY_SCHEMA_VERSION: String = "michibaum.gateway.schema-version"
    const val KEY_HASH: String = "michibaum.gateway.hash"
    const val KEY_CONFIG: String = "michibaum.gateway.config"
    const val DEFAULT_SITEMAP_ENDPOINT_PATH: String = "/actuator/sitemap"
}

data class GatewayServiceMetadata(
    val schemaVersion: Int = GatewayMetadataConstants.CURRENT_SCHEMA_VERSION,
    val serviceId: String,
    val routes: List<RouteDefinitionMetadata> = emptyList(),
    val cors: CorsMetadata = CorsMetadata(),
    val sitemap: SitemapMetadata = SitemapMetadata(),
    val robots: RobotsMetadata = RobotsMetadata()
)

data class RouteDefinitionMetadata(
    val id: String? = null,
    val hosts: List<String> = emptyList(),
    val paths: List<String> = emptyList(),
    val order: Int = 0,
    val requiredPermissions: List<Permissions> = emptyList(),
    val circuitBreaker: CircuitBreakerMetadata? = null
)

data class CircuitBreakerMetadata(
    val timeoutSeconds: Long = 10,
    val slidingWindowSize: Int = 50,
    val minimumNumberOfCalls: Int = 20,
    val failureRateThreshold: Float = 50.0f,
    val waitDurationInOpenStateSeconds: Long = 10,
    val permittedNumberOfCallsInHalfOpenState: Int = 10,
    val slowCallRateThreshold: Float = 50.0f,
    val slowCallDurationThresholdSeconds: Long = 5
)

data class CorsMetadata(
    val allowedOriginPatterns: List<String> = emptyList(),
    val allowedMethods: List<String> = listOf("*"),
    val allowedHeaders: List<String> = listOf("*"),
    val allowCredentials: Boolean = true
)

data class SitemapMetadata(
    val staticLocations: List<String> = emptyList(),
    val endpointPath: String? = null
)

data class RobotsMetadata(
    val crawlers: List<CrawlerRuleMetadata> = emptyList()
)

data class CrawlerRuleMetadata(
    val userAgent: String = "*",
    val allowed: List<String> = emptyList(),
    val disallowed: List<String> = emptyList()
)
