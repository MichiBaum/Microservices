package com.michibaum.gateway_discovery.publisher

import com.michibaum.gateway_discovery.model.CorsMetadata
import com.michibaum.gateway_discovery.model.GatewayMetadataConstants
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RobotsMetadata
import com.michibaum.gateway_discovery.model.RouteDefinitionMetadata
import com.michibaum.gateway_discovery.model.SitemapMetadata
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("gateway.publish")
data class GatewayPublishProperties(
    val enabled: Boolean = true,
    val serviceId: String? = null,
    val routes: List<RouteDefinitionMetadata> = emptyList(),
    val cors: CorsMetadata = CorsMetadata(),
    val sitemap: SitemapMetadata = SitemapMetadata(),
    val robots: RobotsMetadata = RobotsMetadata()
) {
    fun toMetadata(resolvedServiceId: String, hasSitemapUrlProvider: Boolean): GatewayServiceMetadata {
        require(resolvedServiceId.isNotBlank()) {
            "Resolved serviceId for gateway metadata publication must not be blank"
        }
        routes.forEachIndexed { index, route ->
            require(route.hosts.isNotEmpty() || route.paths.isNotEmpty()) {
                "Route at index $index for service '$resolvedServiceId' must declare at least one host or path pattern"
            }
            require(route.hosts.none { it.isBlank() }) {
                "Route at index $index for service '$resolvedServiceId' contains a blank host pattern"
            }
            require(route.paths.none { it.isBlank() }) {
                "Route at index $index for service '$resolvedServiceId' contains a blank path pattern"
            }
        }

        val resolvedEndpointPath = sitemap.endpointPath?.takeIf { it.isNotBlank() }
            ?: if (hasSitemapUrlProvider) GatewayMetadataConstants.DEFAULT_SITEMAP_ENDPOINT_PATH else null

        return GatewayServiceMetadata(
            schemaVersion = GatewayMetadataConstants.CURRENT_SCHEMA_VERSION,
            serviceId = resolvedServiceId,
            routes = routes,
            cors = cors,
            sitemap = sitemap.copy(endpointPath = resolvedEndpointPath),
            robots = robots
        )
    }
}
