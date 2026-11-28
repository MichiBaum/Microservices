package com.michibaum.gatewayservice.app.robotstxt

import com.michibaum.gatewayservice.config.ServiceGatewayConfigStore
import org.springframework.stereotype.Service

@Service
class RobotsTxtService(
    private val robotsTxtProperties: RobotsTxtProperties,
    private val serviceGatewayConfigStore: ServiceGatewayConfigStore = ServiceGatewayConfigStore(null)
) {
    fun generateWith(host: String?): String {
        val mergedCrawlers = LinkedHashMap<String, MutableCrawlerRules>()

        for (crawler in robotsTxtProperties.crawlers) {
            val ua = crawler.userAgent.trim()
            if (ua.isEmpty()) continue
            val bucket = mergedCrawlers.getOrPut(ua) { MutableCrawlerRules() }
            crawler.allowed.map { it.trim() }.filter { it.isNotEmpty() }.forEach { bucket.allowed.add(it) }
            crawler.disallowed.map { it.trim() }.filter { it.isNotEmpty() }.forEach { bucket.disallowed.add(it) }
        }

        val discoveredServices = serviceGatewayConfigStore.currentServices().sortedBy { it.serviceId }
        for (service in discoveredServices) {
            for (crawler in service.robots.crawlers) {
                val ua = crawler.userAgent.trim()
                if (ua.isEmpty()) continue
                val bucket = mergedCrawlers.getOrPut(ua) { MutableCrawlerRules() }
                crawler.allowed.map { it.trim() }.filter { it.isNotEmpty() }.forEach { bucket.allowed.add(it) }
                crawler.disallowed.map { it.trim() }.filter { it.isNotEmpty() }.forEach { bucket.disallowed.add(it) }
            }
        }

        val builder = StringBuilder()

        for ((userAgent, rules) in mergedCrawlers) {
            builder.appendLine("User-agent: $userAgent")

            for (allowedPath in rules.allowed) {
                builder.appendLine("Allow: $allowedPath")
            }

            for (disallowedPath in rules.disallowed) {
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

    private class MutableCrawlerRules(
        val allowed: LinkedHashSet<String> = LinkedHashSet(),
        val disallowed: LinkedHashSet<String> = LinkedHashSet()
    )
}