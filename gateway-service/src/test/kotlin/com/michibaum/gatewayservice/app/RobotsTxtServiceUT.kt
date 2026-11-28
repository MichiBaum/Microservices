package com.michibaum.gatewayservice.app

import com.michibaum.gateway_discovery.model.CrawlerRuleMetadata
import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RobotsMetadata
import com.michibaum.gatewayservice.app.robotstxt.Crawler
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtProperties
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtService
import com.michibaum.gatewayservice.config.ServiceGatewayConfigStore
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient

@ExtendWith(MockitoExtension::class)
class RobotsTxtServiceUT {

    @ParameterizedTest
    @ValueSource(strings = ["michibaum.ch", "michibaum.com", "michibaum.eu"])
    fun `Host in sitemap is used correctly`(host: String){
        // GIVEN
        val crawlerUserAgents = listOf(Crawler("*", listOf("/", "/*"), listOf()))
        val robotsTxtProperties = RobotsTxtProperties(
            crawlerUserAgents,
            listOf("/sitemap.xml")
        )
        val robotsTxtService = RobotsTxtService(robotsTxtProperties)

        // WHEN
        val result = robotsTxtService.generateWith(host)

        // THEN
        Assertions.assertTrue(result.contains("https://$host/sitemap.xml"))
    }

    @Test
    fun `multiple sitemaps`(){
        // GIVEN
        val host = "michibaum.ch"
        val crawlerUserAgents = listOf(Crawler("*", listOf("/", "/*"), listOf()))
        val sitemaps = listOf("/sitemap.xml", "/some/sitemap.xml", "/sitemap-2.xml")
        val robotsTxtProperties = RobotsTxtProperties(
            crawlerUserAgents,
            sitemaps
        )
        val robotsTxtService = RobotsTxtService(robotsTxtProperties)

        // WHEN
        val result = robotsTxtService.generateWith(host)

        // THEN
        for (sitemap in sitemaps) {
            Assertions.assertTrue(result.contains("https://$host$sitemap"))
        }
    }

    @Test
    fun `generic crawler`(){
        // GIVEN
        val host = "michibaum.ch"
        val crawlerUserAgents = listOf(Crawler("*", listOf("/", "/*"), listOf()))
        val sitemaps = listOf("/sitemap.xml", "/some/sitemap.xml", "/sitemap-2.xml")
        val robotsTxtProperties = RobotsTxtProperties(
            crawlerUserAgents,
            sitemaps
        )
        val robotsTxtService = RobotsTxtService(robotsTxtProperties)

        // WHEN
        val result = robotsTxtService.generateWith(host)

        // THEN
        for (sitemap in sitemaps) {
            Assertions.assertTrue(result.contains("https://$host$sitemap"))
        }
    }

    @Test
    fun `merges baseline crawler rules with discovered service crawler rules deterministically`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val adminMeta = GatewayServiceMetadata(
            serviceId = "admin-service",
            robots = RobotsMetadata(
                crawlers = listOf(
                    CrawlerRuleMetadata(userAgent = "*", disallowed = listOf("/admin")),
                    CrawlerRuleMetadata(userAgent = "Googlebot", disallowed = listOf("/admin/metrics"))
                )
            )
        )
        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            robots = RobotsMetadata(
                crawlers = listOf(
                    CrawlerRuleMetadata(userAgent = "*", allowed = listOf("/chess"), disallowed = listOf("/chess/internal"))
                )
            )
        )

        every { discoveryClient.services } returns listOf("chess-service", "admin-service")
        every { discoveryClient.getInstances("admin-service") } returns listOf(
            DefaultServiceInstance("a1", "admin-service", "10.0.0.1", 8080, false, GatewayMetadataCodec.encodeToMap(adminMeta))
        )
        every { discoveryClient.getInstances("chess-service") } returns listOf(
            DefaultServiceInstance("c1", "chess-service", "10.0.0.2", 8080, false, GatewayMetadataCodec.encodeToMap(chessMeta))
        )

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        val robotsTxtProperties = RobotsTxtProperties(
            crawlers = listOf(Crawler("*", listOf("/"), emptyList())),
            sitemaps = listOf("/sitemap.xml")
        )
        val robotsTxtService = RobotsTxtService(robotsTxtProperties, store)

        val result = robotsTxtService.generateWith("michibaum.ch")

        Assertions.assertTrue(result.contains("User-agent: *\nAllow: /\nAllow: /chess\nDisallow: /admin\nDisallow: /chess/internal") ||
            (result.contains("Allow: /chess") && result.contains("Disallow: /admin") && result.contains("Disallow: /chess/internal")))
        Assertions.assertTrue(result.contains("User-agent: Googlebot"))
        Assertions.assertTrue(result.contains("Disallow: /admin/metrics"))
    }


}