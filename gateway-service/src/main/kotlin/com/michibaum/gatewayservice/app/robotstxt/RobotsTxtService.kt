package com.michibaum.gatewayservice.app.robotstxt

import org.springframework.stereotype.Service

@Service
class RobotsTxtService(
    private val robotsTxtProperties: RobotsTxtProperties
) {
    fun generateWith(host: String?): String {
        val builder = StringBuilder()

        for (crawler in robotsTxtProperties.crawlers) {
            builder.appendLine("User-agent: ${crawler.userAgent}")

            for (allowedPath in crawler.allowed) {
                builder.appendLine("Allow: $allowedPath")
            }

            for (disallowedPath in crawler.disallowed) {
                builder.appendLine("Disallow: $disallowedPath")
            }

            builder.appendLine() // Add a blank line after each crawler's rules
        }

        if (host != null) {
            for (sitemap in robotsTxtProperties.sitemaps) {
                builder.appendLine("Sitemap: https://$host$sitemap")
            }
        }

        return builder.toString().trimEnd() // Trim to remove any trailing newlines
    }


}