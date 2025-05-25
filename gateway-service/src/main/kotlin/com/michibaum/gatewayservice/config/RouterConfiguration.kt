package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
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
            .route(host("admin.michibaum.*"), applyCircuitBreaker(lb("admin-service").apply(http()), "admin-service", circuitBreakerFactory))
            .route(host("zipkin.michibaum.*")){request -> 
                request.authenticateWithCircuitBreaker(servicesProperties.zipkinUrl, authFilter, circuitBreakerFactory, "zipkin", Permissions.ADMIN_SERVICE)
            }
            .route(host("grafana.michibaum.*")){request ->
                request.authenticateWithCircuitBreaker(servicesProperties.grafanaUrl, authFilter, circuitBreakerFactory, "grafana", Permissions.ADMIN_SERVICE)
            }
            .route(host("prometheus.michibaum.*")){request ->
                request.authenticateWithCircuitBreaker(servicesProperties.prometheusUrl, authFilter, circuitBreakerFactory, "prometheus", Permissions.ADMIN_SERVICE)
            }
            .route(host("registry.michibaum.*"), applyCircuitBreaker(lb("registry-service").apply(http()), "registry-service", circuitBreakerFactory))
            .route(host("authentication.michibaum.*"), applyCircuitBreaker(lb("authentication-service").apply(http()), "authentication-service", circuitBreakerFactory))
            .route(host("usermanagement.michibaum.*"), applyCircuitBreaker(lb("usermanagement-service").apply(http()), "usermanagement-service", circuitBreakerFactory))
            .route(host("chess.michibaum.*"), applyCircuitBreaker(lb("chess-service").apply(http()), "chess-service", circuitBreakerFactory))
            .route(host("fitness.michibaum.*"), applyCircuitBreaker(lb("fitness-service").apply(http()), "fitness-service", circuitBreakerFactory))
            .route(host("music.michibaum.*"), applyCircuitBreaker(lb("music-service").apply(http()), "music-service", circuitBreakerFactory))

            // Catch-all for main website (Lowest priority, must come last)
            .route(host("michibaum.*"), applyCircuitBreaker(lb("website-service").apply(http()), "website-service", circuitBreakerFactory))
            .build()
    }

}

@ConfigurationProperties(prefix = "management.services")
data class ServicesProperties(
    val zipkinUrl: URI,
    val grafanaUrl: URI,
    val prometheusUrl: URI
)

fun ServerRequest.authenticate(redirect: URI, authFilter: ServletAuthenticationFilter, vararg requiredPermissions: Permissions): ServerResponse {
    val authentication = authFilter.getAuthentication(servletRequest())
    return if (authentication == null || !authentication.isAuthenticated || !authentication.authorities.map { it.authority }.containsAll(requiredPermissions.toList().map { it.name })) {
        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
    } else {
        http(redirect).handle(this)
    }
}

/**
 * Applies a circuit breaker to an authentication handler.
 * @param redirect The URI to redirect to
 * @param authFilter The authentication filter
 * @param circuitBreakerFactory The circuit breaker factory
 * @param serviceName The name of the service (used as the circuit breaker ID)
 * @param requiredPermissions The required permissions
 * @return A handler function wrapped with a circuit breaker
 */
fun ServerRequest.authenticateWithCircuitBreaker(
    redirect: URI,
    authFilter: ServletAuthenticationFilter,
    circuitBreakerFactory: CircuitBreakerFactory<*, *>,
    serviceName: String,
    vararg requiredPermissions: Permissions
): ServerResponse {
    val authentication = authFilter.getAuthentication(servletRequest())
    return if (authentication == null || !authentication.isAuthenticated || !authentication.authorities.map { it.authority }.containsAll(requiredPermissions.toList().map { it.name })) {
        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
    } else {
        val circuitBreaker = circuitBreakerFactory.create("$serviceName-circuit-breaker")
        try {
            circuitBreaker.run({ http(redirect).handle(this) }, { throwable ->
                // Fallback response when circuit is open or call fails
                ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service $serviceName is currently unavailable. Please try again later.")
            })
        } catch (e: Exception) {
            ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request: ${e.message}")
        }
    }
}

/**
 * Applies a circuit breaker to a handler function.
 * @param handlerFunction The handler function to apply the circuit breaker to
 * @param serviceName The name of the service (used as the circuit breaker ID)
 * @param circuitBreakerFactory The circuit breaker factory
 * @return A handler function wrapped with a circuit breaker
 */
fun applyCircuitBreaker(
    handlerFunction: org.springframework.web.servlet.function.HandlerFunction<ServerResponse>,
    serviceName: String,
    circuitBreakerFactory: CircuitBreakerFactory<*, *>
): org.springframework.web.servlet.function.HandlerFunction<ServerResponse> {
    val circuitBreaker = circuitBreakerFactory.create("$serviceName-circuit-breaker")
    return org.springframework.web.servlet.function.HandlerFunction { request ->
        try {
            circuitBreaker.run({ handlerFunction.handle(request) }, { throwable ->
                // Fallback response when circuit is open or call fails
                ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service $serviceName is currently unavailable. Please try again later.")
            })
        } catch (e: Exception) {
            ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request: ${e.message}")
        }
    }
}
