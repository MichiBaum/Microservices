package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.stereotype.Service

@Service
class SitemapXmlService(
    private val sitemapXmlProperties: SitemapXmlProperties,
    private val dataLocationsFetcher: DataLocationsFetcher
) {

    fun generateWith(host: String): String {
        // Sitemap size limits: All formats limit a single sitemap to 50MB (uncompressed) or 50,000 URLs.
        val baseUrl = "https://$host"

        val builder = StringBuilder()
        builder.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        builder.appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")

        // Add each location as a URL entry
        for (location in sitemapXmlProperties.locations) {
            builder.appendLine("  <url>")
            builder.appendLine("    <loc>$baseUrl$location</loc>")
            builder.appendLine("  </url>")
        }

        for (location in sitemapXmlProperties.dataLocations) {
            val data = dataLocationsFetcher.fetch(location.dataLocation)
            for (id in data) {
                builder.appendLine("  <url>")
                builder.appendLine("    <loc>$baseUrl${location.staticPart}</loc>".replace("{replace}", id))
                builder.appendLine("  </url>")
            }
        }

        builder.appendLine("</urlset>")
        return builder.toString()
    }
}