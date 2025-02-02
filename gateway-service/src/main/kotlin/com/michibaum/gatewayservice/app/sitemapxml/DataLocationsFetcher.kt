package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.stereotype.Component

@Component
class DataLocationsFetcher(
    private val fetchers: List<DataLocationFetcher>
) {

    fun fetch(dataLocation: DataLocation): List<String> {
        val fetcher = fetchers.firstOrNull { it.supports(dataLocation) }
            ?: throw IllegalArgumentException("No fetcher found for dataLocation: $dataLocation")

        return fetcher.fetch()
    }

}