package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sitemap-xml")
data class SitemapXmlProperties(
    val locations: List<String>,
    val dataLocations: List<Location>
)

data class Location(
    val staticPart: String,
    val dataLocation: DataLocation
)

enum class DataLocation {
    CHESS_EVENTS,
    CHESS_OPENINGS
}