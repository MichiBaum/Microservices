package com.michibaum.gatewayservice.app

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.michibaum.authentication_starter.AdminServiceCredentials
import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.SitemapMetadata
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlProperties
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlService
import com.michibaum.gatewayservice.config.ServiceGatewayConfigStore
import io.micrometer.observation.ObservationRegistry
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.web.client.RestClient
import java.util.Base64

class SitemapXmlServiceUT {

    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(wireMockConfig().dynamicPort())
        wireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `aggregates static locations and fetches dynamic urls from actuator sitemap with basic auth`() {
        wireMockServer.stubFor(
            get(urlEqualTo("/actuator/sitemap"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"urls":["/chess/events/e1","/chess/openings/o1","/chess"]}""")
                )
        )

        val discoveryClient = mockk<DiscoveryClient>()
        val loadBalancerClient = mockk<LoadBalancerClient>()

        val websiteMeta = GatewayServiceMetadata(
            serviceId = "website-service",
            sitemap = SitemapMetadata(staticLocations = listOf("/", "/about-me"))
        )
        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            sitemap = SitemapMetadata(
                staticLocations = listOf("/chess"),
                endpointPath = "/actuator/sitemap"
            )
        )
        val chessInstance = DefaultServiceInstance(
            "chess-1",
            "chess-service",
            "localhost",
            wireMockServer.port(),
            false,
            GatewayMetadataCodec.encodeToMap(chessMeta)
        )

        every { discoveryClient.services } returns listOf("website-service", "chess-service")
        every { discoveryClient.getInstances("website-service") } returns listOf(
            DefaultServiceInstance("web-1", "website-service", "localhost", 8081, false, GatewayMetadataCodec.encodeToMap(websiteMeta))
        )
        every { discoveryClient.getInstances("chess-service") } returns listOf(chessInstance)
        every { loadBalancerClient.choose("chess-service") } returns chessInstance

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        val service = SitemapXmlService(
            sitemapXmlProperties = SitemapXmlProperties(https = true, locations = listOf("/imprint")),
            serviceGatewayConfigStore = store,
            loadBalancerClient = loadBalancerClient,
            restClientBuilder = RestClient.builder(),
            adminServiceCredentials = AdminServiceCredentials(username = "admin_user", password = "admin_password"),
            observationRegistry = ObservationRegistry.create()
        )

        val xml = service.generateWith("michibaum.ch")

        assertThat(xml).contains("<loc>https://michibaum.ch/imprint</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/about-me</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/chess</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/chess/events/e1</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/chess/openings/o1</loc>")
        // Deduplicated '/chess'
        assertThat(xml.split("<loc>https://michibaum.ch/chess</loc>").size - 1).isEqualTo(1)

        val expectedBasic = "Basic " + Base64.getEncoder().encodeToString("admin_user:admin_password".toByteArray())
        wireMockServer.verify(
            getRequestedFor(urlEqualTo("/actuator/sitemap"))
                .withHeader("Authorization", equalTo(expectedBasic))
        )
    }

    @Test
    fun `gracefully degrades when downstream actuator sitemap returns 500`() {
        wireMockServer.stubFor(
            get(urlEqualTo("/actuator/sitemap"))
                .willReturn(aResponse().withStatus(500))
        )

        val discoveryClient = mockk<DiscoveryClient>()
        val loadBalancerClient = mockk<LoadBalancerClient>()

        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            sitemap = SitemapMetadata(
                staticLocations = listOf("/chess"),
                endpointPath = "/actuator/sitemap"
            )
        )
        val chessInstance = DefaultServiceInstance(
            "chess-1",
            "chess-service",
            "localhost",
            wireMockServer.port(),
            false,
            GatewayMetadataCodec.encodeToMap(chessMeta)
        )

        every { discoveryClient.services } returns listOf("chess-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(chessInstance)
        every { loadBalancerClient.choose("chess-service") } returns chessInstance

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        val service = SitemapXmlService(
            sitemapXmlProperties = SitemapXmlProperties(https = true, locations = listOf("/")),
            serviceGatewayConfigStore = store,
            loadBalancerClient = loadBalancerClient,
            restClientBuilder = RestClient.builder(),
            adminServiceCredentials = AdminServiceCredentials(username = "admin_user", password = "admin_password"),
            observationRegistry = ObservationRegistry.create()
        )

        val xml = service.generateWith("michibaum.ch")

        assertThat(xml).contains("<loc>https://michibaum.ch/</loc>")
        assertThat(xml).contains("<loc>https://michibaum.ch/chess</loc>")
        assertThat(xml).endsWith("</urlset>\n").contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    }
}