package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RouteDefinitionMetadata
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import com.michibaum.permission_library.Permissions
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

class DynamicGatewayRouterUT {

    private fun createCircuitBreakerFactory(): Resilience4JCircuitBreakerFactory {
        val registry = io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry.ofDefaults()
        val factory = mockk<Resilience4JCircuitBreakerFactory>(relaxed = true)
        every { factory.circuitBreakerRegistry } returns registry
        every { factory.create(any()) } answers {
            object : org.springframework.cloud.client.circuitbreaker.CircuitBreaker {
                override fun <T> run(
                    toRun: java.util.function.Supplier<T>,
                    fallback: java.util.function.Function<Throwable?, T>
                ): T {
                    return try {
                        toRun.get()
                    } catch (t: Throwable) {
                        fallback.apply(t)
                    }
                }
            }
        }
        return factory
    }

    private fun buildServerRequest(
        method: String = "GET",
        scheme: String = "https",
        host: String,
        path: String = "/",
        query: String? = null
    ): ServerRequest {
        val servletRequest = MockHttpServletRequest(method, path).apply {
            this.scheme = scheme
            this.serverName = host.substringBefore(':')
            this.serverPort = if (scheme == "https") 443 else 80
            this.queryString = query
            addHeader("Host", host)
        }
        return ServerRequest.create(servletRequest, emptyList())
    }

    @Test
    fun `handles static redirects, external authenticated routes, dynamic Eureka priority, and hot-reload`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val authFilter = mockk<ServletAuthenticationFilter>()
        val robotsController = mockk<RobotsTxtController>()
        val sitemapController = mockk<SitemapXmlController>()

        every { robotsController.robotsTxt() } returns HandlerFunction {
            ServerResponse.ok().body("robots-ok")
        }
        every { sitemapController.sitemapXml() } returns HandlerFunction {
            ServerResponse.ok().body("sitemap-ok")
        }
        every { authFilter.getAuthentication(any()) } returns null

        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*"), order = 0))
        )
        val websiteMeta = GatewayServiceMetadata(
            serviceId = "website-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("michibaum.*"), order = 1000))
        )

        val chessInstance = DefaultServiceInstance(
            "chess-1", "chess-service", "10.0.0.1", 8080, false, GatewayMetadataCodec.encodeToMap(chessMeta)
        )
        val websiteInstance = DefaultServiceInstance(
            "web-1", "website-service", "10.0.0.2", 8080, false, GatewayMetadataCodec.encodeToMap(websiteMeta)
        )

        every { discoveryClient.services } returns listOf("website-service", "chess-service")
        every { discoveryClient.getInstances("website-service") } returns listOf(websiteInstance)
        every { discoveryClient.getInstances("chess-service") } returns listOf(chessInstance)

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        val staticProps = GatewayStaticRoutesProperties(
            redirects = listOf(
                RedirectRouteProperties(
                    hosts = listOf("babymetal.ch"),
                    status = HttpStatus.TEMPORARY_REDIRECT,
                    targetUrl = URI.create("https://babymetal.com")
                ),
                RedirectRouteProperties(
                    hosts = listOf("einsiedeln.beer"),
                    status = HttpStatus.NO_CONTENT
                ),
                RedirectRouteProperties(
                    hosts = listOf("www.michibaum.{tld}"),
                    status = HttpStatus.PERMANENT_REDIRECT,
                    schemeHostTemplate = "{scheme}://michibaum.{tld}",
                    preservePathAndQuery = true
                )
            ),
            externalServices = listOf(
                ExternalServiceRouteProperties(
                    id = "jaeger-service",
                    hosts = listOf("jaeger.michibaum.*"),
                    uri = URI.create("http://localhost:16686"),
                    order = 0,
                    requiredPermissions = listOf(Permissions.ADMIN_SERVICE)
                )
            )
        )

        val routedTargets = mutableListOf<String>()
        val router = DynamicGatewayRouter(
            staticRoutesProperties = staticProps,
            serviceGatewayConfigStore = store,
            robotsTxtController = robotsController,
            sitemapXmlController = sitemapController,
            authFilter = authFilter,
            circuitBreakerFactory = createCircuitBreakerFactory(),
            loadBalancerCacheManager = null,
            transportFactory = DownstreamTransportFactory { destination ->
                HandlerFunction {
                    val label = when (destination) {
                        is RouteDestination.LoadBalanced -> "lb:${destination.serviceId}"
                        is RouteDestination.StaticUri -> "uri:${destination.uri}"
                    }
                    routedTargets.add(label)
                    ServerResponse.ok().body(label)
                }
            }
        )
        val delegatingRouter = RouterConfiguration().gatewayRoutes(router)

        // 1. Domain redirects take precedence even for /robots.txt on www.michibaum.ch
        val wwwReq = buildServerRequest(host = "www.michibaum.ch", path = "/chess/events", query = "page=2")
        val wwwResp = delegatingRouter.route(wwwReq).orElseThrow().handle(wwwReq)
        assertThat(wwwResp.statusCode()).isEqualTo(HttpStatus.PERMANENT_REDIRECT)
        assertThat(wwwResp.headers().location).isEqualTo(URI.create("https://michibaum.ch/chess/events?page=2"))

        val babymetalReq = buildServerRequest(host = "babymetal.ch", path = "/tour")
        val babymetalResp = delegatingRouter.route(babymetalReq).orElseThrow().handle(babymetalReq)
        assertThat(babymetalResp.statusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT)
        assertThat(babymetalResp.headers().location).isEqualTo(URI.create("https://babymetal.com"))

        val einsiedelnReq = buildServerRequest(host = "einsiedeln.beer")
        val einsiedelnResp = delegatingRouter.route(einsiedelnReq).orElseThrow().handle(einsiedelnReq)
        assertThat(einsiedelnResp.statusCode()).isEqualTo(HttpStatus.NO_CONTENT)

        // 2. External route enforces ADMIN_SERVICE permission
        val jaegerUnauthReq = buildServerRequest(host = "jaeger.michibaum.ch", path = "/search")
        val jaegerUnauthResp = delegatingRouter.route(jaegerUnauthReq).orElseThrow().handle(jaegerUnauthReq)
        assertThat(jaegerUnauthResp.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)

        val adminAuth = TestingAuthenticationToken(
            "admin",
            "pw",
            listOf(SimpleGrantedAuthority(Permissions.ADMIN_SERVICE.name))
        )
        every { authFilter.getAuthentication(any()) } returns adminAuth
        val jaegerAuthReq = buildServerRequest(host = "jaeger.michibaum.ch", path = "/search")
        val jaegerAuthResp = delegatingRouter.route(jaegerAuthReq).orElseThrow().handle(jaegerAuthReq)
        assertThat(jaegerAuthResp.statusCode()).isEqualTo(HttpStatus.OK)
        assertThat(routedTargets.last()).isEqualTo("uri:http://localhost:16686")

        // 3. Specific subdomain wins over website-service catch-all
        val chessReq = buildServerRequest(host = "chess.michibaum.ch", path = "/api/events")
        val chessResp = delegatingRouter.route(chessReq).orElseThrow().handle(chessReq)
        assertThat(chessResp.statusCode()).isEqualTo(HttpStatus.OK)
        assertThat(routedTargets.last()).isEqualTo("lb:chess-service")

        val mainWebReq = buildServerRequest(host = "michibaum.ch", path = "/home")
        val mainWebResp = delegatingRouter.route(mainWebReq).orElseThrow().handle(mainWebReq)
        assertThat(mainWebResp.statusCode()).isEqualTo(HttpStatus.OK)
        assertThat(routedTargets.last()).isEqualTo("lb:website-service")

        // 4. Hot-reload: newly discovered music-service becomes active without recreating delegatingRouter
        val musicMeta = GatewayServiceMetadata(
            serviceId = "music-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("music.michibaum.*"), order = 0))
        )
        val musicInstance = DefaultServiceInstance(
            "music-1", "music-service", "10.0.0.3", 8080, false, GatewayMetadataCodec.encodeToMap(musicMeta)
        )
        every { discoveryClient.services } returns listOf("website-service", "chess-service", "music-service")
        every { discoveryClient.getInstances("music-service") } returns listOf(musicInstance)

        store.onHeartbeat()

        val musicReq = buildServerRequest(host = "music.michibaum.ch", path = "/api/tracks")
        val musicResp = delegatingRouter.route(musicReq).orElseThrow().handle(musicReq)
        assertThat(musicResp.statusCode()).isEqualTo(HttpStatus.OK)
        assertThat(routedTargets.last()).isEqualTo("lb:music-service")
    }

    @Test
    fun `retained last-known-good route returns 503 Service Unavailable instead of falling through to catch-all`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val authFilter = mockk<ServletAuthenticationFilter>()
        val robotsController = mockk<RobotsTxtController>()
        val sitemapController = mockk<SitemapXmlController>()

        every { robotsController.robotsTxt() } returns HandlerFunction { ServerResponse.ok().build() }
        every { sitemapController.sitemapXml() } returns HandlerFunction { ServerResponse.ok().build() }
        every { authFilter.getAuthentication(any()) } returns null

        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*"), order = 0))
        )
        val websiteMeta = GatewayServiceMetadata(
            serviceId = "website-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("michibaum.*"), order = 1000))
        )

        every { discoveryClient.services } returns listOf("chess-service", "website-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(
            DefaultServiceInstance("c1", "chess-service", "10.0.0.1", 8080, false, GatewayMetadataCodec.encodeToMap(chessMeta))
        )
        every { discoveryClient.getInstances("website-service") } returns listOf(
            DefaultServiceInstance("w1", "website-service", "10.0.0.2", 8080, false, GatewayMetadataCodec.encodeToMap(websiteMeta))
        )

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        // Simulate chess-service crashing (0 instances in Eureka)
        every { discoveryClient.services } returns listOf("website-service")
        store.onHeartbeat()

        val router = DynamicGatewayRouter(
            staticRoutesProperties = GatewayStaticRoutesProperties(),
            serviceGatewayConfigStore = store,
            robotsTxtController = robotsController,
            sitemapXmlController = sitemapController,
            authFilter = authFilter,
            circuitBreakerFactory = createCircuitBreakerFactory(),
            loadBalancerCacheManager = null,
            transportFactory = DownstreamTransportFactory { destination ->
                HandlerFunction {
                    if (destination is RouteDestination.LoadBalanced && destination.serviceId == "chess-service") {
                        throw IllegalStateException("No instances available for chess-service")
                    }
                    ServerResponse.ok().body("website")
                }
            }
        )

        val chessReq = buildServerRequest(host = "chess.michibaum.ch", path = "/chess")
        val response = router.currentRouter().route(chessReq).orElseThrow().handle(chessReq)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }
}
