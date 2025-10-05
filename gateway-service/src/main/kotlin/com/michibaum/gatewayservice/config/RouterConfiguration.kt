package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import com.michibaum.gatewayservice.config.Service.*
import com.michibaum.permission_library.Permissions
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.host
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
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
            .route(host("babymetal.ch")) { _ -> // TODO implement custom fansite for babymetal.ch
//                val path = req.uri().path
//                val query = req.uri().rawQuery?.let { "?$it" } ?: ""
//                val uriString = "https://babymetal.com$path$query"
                ServerResponse.temporaryRedirect(URI.create("https://babymetal.com")).build()
            }

            .route(host("hanabie.ch")) { _ -> // TODO implement custom fansite for hanabie.ch
                ServerResponse.temporaryRedirect(URI.create("https://hanabie.jp/en")).build()
            }

            .route(host("baumberger-software.ch", "baumberger-software.com")) { _ -> // TODO implement custom service
                ServerResponse.temporaryRedirect(URI.create("https://michibaum.ch/about-me")).build()
            }

            .route(host("einsiedeln.beer")) { _ -> // TODO implement custom service
                ServerResponse.noContent().build()
            }

            .route(host("einsiedeln.shop", "einsiedeln.store")) { _ -> // TODO implement custom service
                ServerResponse.noContent().build()
            }
            
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
            .route(host("admin.michibaum.*"), applyCircuitBreaker(lb(ADMIN.id).apply(http()),
                ADMIN, circuitBreakerFactory))
            .route(host("registry.michibaum.*"), applyCircuitBreaker(lb(REGISTRY.id).apply(http()),
                REGISTRY, circuitBreakerFactory))
            .route(host("authentication.michibaum.*"), applyCircuitBreaker(lb(AUTHENTICATION.id).apply(http()),
                AUTHENTICATION, circuitBreakerFactory))
            .route(host("usermanagement.michibaum.*"), applyCircuitBreaker(lb(USERMANAGEMENT.id).apply(http()),
                USERMANAGEMENT, circuitBreakerFactory))
            .route(host("chess.michibaum.*"), applyCircuitBreaker(lb(CHESS.id).apply(http()),
                CHESS, circuitBreakerFactory))
            .route(host("fitness.michibaum.*"), applyCircuitBreaker(lb(FITNESS.id).apply(http()),
                FITNESS, circuitBreakerFactory))
            .route(host("music.michibaum.*"), applyCircuitBreaker(lb(MUSIC.id).apply(http()),
                MUSIC, circuitBreakerFactory))

            // Catch-all for main website (Lowest priority, must come last)
            .route(host("michibaum.*"), applyCircuitBreaker(lb(WEBSITE.id).apply(http()),
                WEBSITE, circuitBreakerFactory))
            .build()
    }

}