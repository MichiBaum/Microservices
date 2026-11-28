package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sitemap-xml")
data class SitemapXmlProperties(
    val https: Boolean,
    val locations: List<String> = emptyList()
)