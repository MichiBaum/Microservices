package com.michibaum.gateway_discovery.sitemap

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SitemapActuatorEndpointUT {

    @Test
    fun `aggregates trims filters blanks and deduplicates urls across providers`() {
        val providerOne = SitemapUrlProvider {
            listOf("/chess/events/1", " /chess/events/2 ", "", "   ")
        }
        val providerTwo = SitemapUrlProvider {
            listOf("/chess/events/2", "/chess/openings/1")
        }

        val endpoint = SitemapActuatorEndpoint(listOf(providerOne, providerTwo))
        val response = endpoint.sitemap()

        assertThat(response.urls).containsExactly(
            "/chess/events/1",
            "/chess/events/2",
            "/chess/openings/1"
        )
    }
}
