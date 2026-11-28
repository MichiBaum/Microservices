package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.gateway_discovery.model.CircuitBreakerMetadata
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gatewayservice.app.robotstxt.RobotsTxtController
import com.michibaum.gatewayservice.app.sitemapxml.SitemapXmlController
import com.michibaum.permission_library.Permissions
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.host
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.RequestPredicate
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI
import java.util.concurrent.atomic.AtomicReference

sealed interface RouteDestination {
    data class LoadBalanced(val serviceId: String) : RouteDestination
    data class StaticUri(val uri: URI) : RouteDestination
}

data class CompiledServiceRoute(
    val routeId: String,
    val serviceId: String,
    val hosts: List<String>,
    val paths: List<String>,
    val order: Int,
    val sourcePriority: Int,
    val requiredPermissions: List<Permissions>,
    val destination: RouteDestination,
    val circuitBreaker: CircuitBreakerMetadata?
)

fun interface DownstreamTransportFactory {
    fun createHandler(destination: RouteDestination): HandlerFunction<ServerResponse>
}

@Component
class DynamicGatewayRouter internal constructor(
    private val staticRoutesProperties: GatewayStaticRoutesProperties,
    private val serviceGatewayConfigStore: ServiceGatewayConfigStore,
    private val robotsTxtController: RobotsTxtController,
    private val sitemapXmlController: SitemapXmlController,
    private val authFilter: ServletAuthenticationFilter,
    private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory,
    private val loadBalancerCacheManager: LoadBalancerCacheManager?,
    private val transportFactory: DownstreamTransportFactory
) {

    @Autowired
    constructor(
        staticRoutesProperties: GatewayStaticRoutesProperties,
        serviceGatewayConfigStore: ServiceGatewayConfigStore,
        robotsTxtController: RobotsTxtController,
        sitemapXmlController: SitemapXmlController,
        authFilter: ServletAuthenticationFilter,
        circuitBreakerFactory: Resilience4JCircuitBreakerFactory,
        loadBalancerCacheManager: LoadBalancerCacheManager?
    ) : this(
        staticRoutesProperties = staticRoutesProperties,
        serviceGatewayConfigStore = serviceGatewayConfigStore,
        robotsTxtController = robotsTxtController,
        sitemapXmlController = sitemapXmlController,
        authFilter = authFilter,
        circuitBreakerFactory = circuitBreakerFactory,
        loadBalancerCacheManager = loadBalancerCacheManager,
        transportFactory = DownstreamTransportFactory { destination ->
            when (destination) {
                is RouteDestination.LoadBalanced -> lb(destination.serviceId).apply(http())
                is RouteDestination.StaticUri -> staticUriHttp(destination.uri)
            }
        }
    )

    private val log = LogFactory.getLog(DynamicGatewayRouter::class.java)
    private val templateVarRegex = Regex("\\{([^}]+)}")
    private val routerRef = AtomicReference<RouterFunction<ServerResponse>>(
        compileRouter(serviceGatewayConfigStore.currentServices())
    )

    init {
        serviceGatewayConfigStore.registerListener { snapshot ->
            rebuild(snapshot.metadataList)
        }
    }

    fun currentRouter(): RouterFunction<ServerResponse> = routerRef.get()

    fun rebuild(discoveredServices: List<GatewayServiceMetadata>) {
        val compiled = compileRouter(discoveredServices)
        routerRef.set(compiled)
        log.info("Atomically swapped gateway RouterFunction with ${discoveredServices.size} discovered service(s)")
    }

    private fun compileRouter(discoveredServices: List<GatewayServiceMetadata>): RouterFunction<ServerResponse> {
        val builder = route()

        // 1. Declarative domain redirects & no-content host handlers (highest priority, before robots/sitemap)
        for (redirect in staticRoutesProperties.redirects) {
            if (redirect.hosts.isEmpty()) {
                continue
            }
            builder.route(host(*redirect.hosts.toTypedArray()), buildRedirectHandler(redirect))
        }

        // 2. Gateway-local crawler endpoints
        builder.GET("/robots.txt", robotsTxtController.robotsTxt())
        builder.GET("/sitemap.xml", sitemapXmlController.sitemapXml())

        // 3. External static service routes + discovered Eureka microservice routes sorted by priority
        val allServiceRoutes = buildSortedServiceRoutes(discoveredServices)
        for (compiledRoute in allServiceRoutes) {
            val predicate = buildRequestPredicate(compiledRoute.hosts, compiledRoute.paths) ?: continue
            val transportHandler = transportFactory.createHandler(compiledRoute.destination)
            val securedHandler = requirePermissions(
                delegate = transportHandler,
                authFilter = authFilter,
                requiredPermissions = compiledRoute.requiredPermissions
            )
            val routeTarget = RouteTarget(
                serviceId = compiledRoute.serviceId,
                circuitBreakerId = "${compiledRoute.serviceId}-circuit-breaker",
                circuitBreakerConfig = compiledRoute.circuitBreaker
            )
            val resilientHandler = applyCircuitBreaker(
                handlerFunction = securedHandler,
                routeTarget = routeTarget,
                circuitBreakerFactory = circuitBreakerFactory,
                loadBalancerCacheManager = loadBalancerCacheManager
            )
            builder.route(predicate, resilientHandler)
        }

        return builder.build()
    }

    internal fun buildSortedServiceRoutes(discoveredServices: List<GatewayServiceMetadata>): List<CompiledServiceRoute> {
        val externalRoutes = staticRoutesProperties.externalServices.mapIndexed { idx, ext ->
            CompiledServiceRoute(
                routeId = ext.id.ifBlank { "external-$idx" },
                serviceId = ext.id,
                hosts = ext.hosts,
                paths = ext.paths,
                order = ext.order,
                sourcePriority = 0,
                requiredPermissions = ext.requiredPermissions,
                destination = RouteDestination.StaticUri(ext.uri),
                circuitBreaker = ext.circuitBreaker
            )
        }

        val eurekaRoutes = discoveredServices.flatMap { service ->
            service.routes.mapIndexed { idx, routeDef ->
                CompiledServiceRoute(
                    routeId = routeDef.id?.takeIf { it.isNotBlank() } ?: "${service.serviceId}-$idx",
                    serviceId = service.serviceId,
                    hosts = routeDef.hosts,
                    paths = routeDef.paths,
                    order = routeDef.order,
                    sourcePriority = 1,
                    requiredPermissions = routeDef.requiredPermissions,
                    destination = RouteDestination.LoadBalanced(service.serviceId),
                    circuitBreaker = routeDef.circuitBreaker
                )
            }
        }

        return (externalRoutes + eurekaRoutes).sortedWith(
            compareBy<CompiledServiceRoute> { it.order }
                .thenByDescending { computeHostSpecificity(it.hosts) }
                .thenByDescending { computePathSpecificity(it.paths) }
                .thenBy { it.sourcePriority }
                .thenBy { it.serviceId }
                .thenBy { it.routeId }
        )
    }

    private fun buildRequestPredicate(hosts: List<String>, paths: List<String>): RequestPredicate? {
        val hostPredicate = hosts.takeIf { it.isNotEmpty() }?.let { host(*it.toTypedArray()) }
        val pathPredicate = paths.takeIf { it.isNotEmpty() }?.let { path(*it.toTypedArray()) }
        return when {
            hostPredicate != null && pathPredicate != null -> hostPredicate.and(pathPredicate)
            hostPredicate != null -> hostPredicate
            pathPredicate != null -> pathPredicate
            else -> null
        }
    }

    private fun computeHostSpecificity(hosts: List<String>): Int {
        if (hosts.isEmpty()) {
            return -1
        }
        return hosts.maxOf { pattern ->
            val firstDynamicIndex = pattern.indexOfFirst { it == '*' || it == '{' || it == '?' }
            val literalPrefix = if (firstDynamicIndex >= 0) pattern.substring(0, firstDynamicIndex) else pattern
            val labelCount = pattern.split('.').count { it.isNotEmpty() && !it.contains('*') && !it.contains('{') }
            (labelCount * 100) + literalPrefix.length
        }
    }

    private fun computePathSpecificity(paths: List<String>): Int {
        if (paths.isEmpty()) {
            return 0
        }
        return paths.maxOf { pattern ->
            val firstDynamicIndex = pattern.indexOfFirst { it == '*' || it == '{' || it == '?' }
            if (firstDynamicIndex >= 0) firstDynamicIndex else pattern.length + 100
        }
    }

    private fun buildRedirectHandler(redirect: RedirectRouteProperties): HandlerFunction<ServerResponse> {
        return HandlerFunction { req ->
            if (redirect.status == HttpStatus.NO_CONTENT ||
                (redirect.targetUrl == null && redirect.schemeHostTemplate == null)
            ) {
                return@HandlerFunction ServerResponse.status(redirect.status).build()
            }

            val baseLocation = when {
                redirect.schemeHostTemplate != null -> {
                    val incomingScheme = req.uri().scheme
                        ?.takeIf { it.isNotBlank() }
                        ?: req.servletRequest().scheme
                        ?: "https"
                    val pathVars = req.pathVariables()
                    templateVarRegex.replace(redirect.schemeHostTemplate) { matchResult ->
                        val key = matchResult.groupValues[1]
                        if (key == "scheme") incomingScheme else pathVars[key] ?: matchResult.value
                    }
                }
                else -> redirect.targetUrl.toString()
            }

            val fullLocation = if (redirect.preservePathAndQuery) {
                val reqPath = req.uri().path.orEmpty()
                val reqQuery = req.uri().rawQuery?.let { "?$it" }.orEmpty()
                "$baseLocation$reqPath$reqQuery"
            } else {
                baseLocation
            }

            val targetUri = URI.create(fullLocation)
            when (redirect.status) {
                HttpStatus.PERMANENT_REDIRECT -> ServerResponse.permanentRedirect(targetUri).build()
                HttpStatus.TEMPORARY_REDIRECT -> ServerResponse.temporaryRedirect(targetUri).build()
                else -> ServerResponse.status(redirect.status).location(targetUri).build()
            }
        }
    }
}
