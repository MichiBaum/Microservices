package com.michibaum.gatewayservice.app

import com.michibaum.gatewayservice.app.robotstxt.Crawler
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtProperties
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.junit.jupiter.MockitoExtension

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


}