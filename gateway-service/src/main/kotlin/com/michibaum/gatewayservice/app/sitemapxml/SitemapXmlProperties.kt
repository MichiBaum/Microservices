package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sitemap-xml")
data class SitemapXmlProperties(
    val locations: List<String>
)
