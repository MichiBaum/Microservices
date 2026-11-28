package com.michibaum.gateway_discovery.sitemap

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation

@Endpoint(id = "sitemap")
class SitemapActuatorEndpoint(
    private val providers: List<SitemapUrlProvider>
) {

    @ReadOperation
    fun sitemap(): SitemapUrlsResponse {
        val urls = providers
            .flatMap { it.provideUrls() }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        return SitemapUrlsResponse(urls = urls)
    }
}
