package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import com.michibaum.gatewayservice.config.CircuitBreakerId.*
import com.michibaum.permission_library.Permissions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.host
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

@Configuration
@EnableConfigurationProperties(value = [ServicesProperties::class])
class RouterConfiguration {

    /**
     * Redirect of routes. Order is important.
     */
    @Bean
    fun gatewayRoutes(
        servicesProperties: ServicesProperties,
        robotsTxtController: RobotsTxtController,
        sitemapXmlController: SitemapXmlController,
        authFilter: ServletAuthenticationFilter,
        circuitBreakerFactory: CircuitBreakerFactory<*, *>
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
            .route(host("admin.michibaum.*"), applyCircuitBreaker(lb(ADMIN.serviceId).apply(http()),
                ADMIN, circuitBreakerFactory))
            .route(host("zipkin.michibaum.*")){request -> 
                request.authenticateWithCircuitBreaker(servicesProperties.zipkinUrl, authFilter, circuitBreakerFactory,
                    ZIPKIN, Permissions.ADMIN_SERVICE)
            }
            .route(host("grafana.michibaum.*")){request ->
                request.authenticateWithCircuitBreaker(servicesProperties.grafanaUrl, authFilter, circuitBreakerFactory,
                    GRAFANA, Permissions.ADMIN_SERVICE)
            }
            .route(host("prometheus.michibaum.*")){request ->
                request.authenticateWithCircuitBreaker(servicesProperties.prometheusUrl, authFilter, circuitBreakerFactory,
                    PROMETHEUS, Permissions.ADMIN_SERVICE)
            }
            .route(host("registry.michibaum.*"), applyCircuitBreaker(lb(REGISTRY.serviceId).apply(http()),
                REGISTRY, circuitBreakerFactory))
            .route(host("authentication.michibaum.*"), applyCircuitBreaker(lb(AUTHENTICATION.serviceId).apply(http()),
                AUTHENTICATION, circuitBreakerFactory))
            .route(host("usermanagement.michibaum.*"), applyCircuitBreaker(lb(USERMANAGEMENT.serviceId).apply(http()),
                USERMANAGEMENT, circuitBreakerFactory))
            .route(host("chess.michibaum.*"), applyCircuitBreaker(lb(CHESS.serviceId).apply(http()),
                CHESS, circuitBreakerFactory))
            .route(host("fitness.michibaum.*"), applyCircuitBreaker(lb(FITNESS.serviceId).apply(http()),
                FITNESS, circuitBreakerFactory))
            .route(host("music.michibaum.*"), applyCircuitBreaker(lb(MUSIC.serviceId).apply(http()),
                MUSIC, circuitBreakerFactory))

            // Catch-all for main website (Lowest priority, must come last)
            .route(host("michibaum.*"), applyCircuitBreaker(lb(WEBSITE.serviceId).apply(http()),
                WEBSITE, circuitBreakerFactory))
            .build()
    }

}