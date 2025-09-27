package com.michibaum.gatewayservice.app

import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtProperties
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [RobotsTxtProperties::class, SitemapXmlProperties::class])
class CrawlerConfiguration 