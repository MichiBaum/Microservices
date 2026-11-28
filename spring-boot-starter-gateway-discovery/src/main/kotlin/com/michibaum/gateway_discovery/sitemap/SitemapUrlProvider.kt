package com.michibaum.gateway_discovery.sitemap

fun interface SitemapUrlProvider {
    fun provideUrls(): List<String>
}

data class SitemapUrlsResponse(
    val urls: List<String>
)
