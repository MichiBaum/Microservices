package com.michibaum.gatewayservice.app.robotstxt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "robots-txt")
data class RobotsTxtProperties(
    val crawlers: List<Crawler>,
    val sitemaps: List<String>
)

data class Crawler(
    val userAgent: String,
    val allowed: List<String> = ArrayList(),
    val disallowed: List<String> = ArrayList()
)