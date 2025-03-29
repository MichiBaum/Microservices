package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import com.michibaum.permission_library.Permissions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.host
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

@Configuration
@EnableConfigurationProperties(value = [ZipkinProperties::class])
class RouterConfiguration {

    /**
     * Redirect of routes. Order is important.
     */
    @Bean
    fun gatewayRoutes(
        zipkinProperties: ZipkinProperties,
        robotsTxtController: RobotsTxtController,
        sitemapXmlController: SitemapXmlController,
        authFilter: ServletAuthenticationFilter
    ): RouterFunction<ServerResponse> {
        return route()
            // Add a redirect for "www.michibaum.*"
            // First because all www should be redirected even robots.txt sitemap.xml
            .route(host("www.michibaum.{tld}")) { req ->
                val tld = req.pathVariable("tld")
                val incomingScheme = req.uri().scheme
                val path = req.uri().path
                val query = req.uri().rawQuery?.let { "?$it" } ?: ""

                val redirectUri = "$incomingScheme://michibaum.$tld$path$query"
                ServerResponse.permanentRedirect(
                    URI.create(redirectUri)
                ).build()
            }

            // Special routes (Highest priority, must come first)
            .GET("/robots.txt", robotsTxtController.robotsTxt())
            .GET("/sitemap.xml", sitemapXmlController.sitemapXml())

            // Specific services (Higher priority)
            .route(host("admin.michibaum.*"), lb("admin-service").apply(http()))
            .route(host("zipkin.michibaum.*")){request ->
                val authentication = authFilter.getAuthentication(request.servletRequest())
                if (authentication == null || !authentication.isAuthenticated || !authentication.authorities.map { it.authority }.contains(Permissions.ADMIN_SERVICE.name)) {
                    ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
                } else {
                    http(zipkinProperties.serviceUrl).handle(request)
                }

            }
            .route(host("registry.michibaum.*"), lb("registry-service").apply(http()))
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

@ConfigurationProperties(prefix = "management.zipkin")
data class ZipkinProperties(
    val serviceUrl: URI
)