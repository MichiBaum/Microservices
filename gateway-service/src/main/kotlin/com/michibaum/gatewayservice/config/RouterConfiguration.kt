package com.michibaum.gatewayservice.config

import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.host
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse

@Configuration
class RouterConfiguration {

    @Bean
    fun gatewayRoutes(
        robotsTxtController: RobotsTxtController,
        sitemapXmlController: SitemapXmlController
    ): RouterFunction<ServerResponse> {
        return route()
            // Special routes (Highest priority, must come first)
            .GET("/robots.txt", robotsTxtController.robotsTxt())
            .GET("/sitemap.xml", sitemapXmlController.sitemapXml())

            // Specific services (Higher priority)
            .route(host("admin.michibaum.*"), lb("admin-service").apply(http()))
            // TODO Authentication
            //  .route(host("registry.michibaum.*"), lb("registry-service").apply(http()))
            .route(host("authentication.michibaum.*"), lb("authentication-service").apply(http()))
            .route(host("usermanagement.michibaum.*"), lb("usermanagement-service").apply(http()))
            .route(host("chess.michibaum.*"), lb("chess-service").apply(http()))
            .route(host("fitness.michibaum.*"), lb("fitness-service").apply(http()))
            .route(host("music.michibaum.*"), lb("music-service").apply(http()))

            // Catch-all for main website (Lowest priority, must come last)
            .route(host("michibaum.*"), lb("website-service").apply(http()))
            .build()
    }

}