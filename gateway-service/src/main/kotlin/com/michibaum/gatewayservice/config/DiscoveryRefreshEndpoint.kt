package com.michibaum.gatewayservice.config

import com.netflix.discovery.EurekaClient
import org.apache.commons.logging.LogFactory
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.time.Instant

/**
 * Operational endpoint allowing operators to force an immediate Eureka registry refresh and
 * LoadBalancer cache eviction, so a stale-cache incident can be recovered from without restarting
 * the gateway pod. Exposed at `/actuator/discoveryRefresh` (POST), following the standard
 * `management.endpoints.web.exposure.include: "*"` configuration already in place.
 */
@Component
@Endpoint(id = "discoveryRefresh")
class DiscoveryRefreshEndpoint(
    private val eurekaClient: EurekaClient?,
    private val loadBalancerCacheManager: LoadBalancerCacheManager?
) {

    private val log = LogFactory.getLog(DiscoveryRefreshEndpoint::class.java)

    @WriteOperation
    fun refresh(): DiscoveryRefreshResult {
        refreshEurekaRegistry()

        val evictedCaches = loadBalancerCacheManager?.cacheNames?.toList().orEmpty()
        evictedCaches.forEach { cacheName ->
            loadBalancerCacheManager?.getCache(cacheName)?.clear()
        }

        return DiscoveryRefreshResult(
            evictedCaches = evictedCaches,
            refreshedAt = Instant.now()
        )
    }

    /**
     * Forces the underlying Eureka client to re-fetch the registry. `refreshRegistry()` is an
     * internal, package-private method on [com.netflix.discovery.DiscoveryClient], so it is
     * invoked reflectively. If it cannot be invoked (e.g. Eureka is temporarily unreachable, or a
     * future client version removes/renames it), this fails gracefully: the LoadBalancer cache
     * eviction below still provides the main recovery mechanism.
     */
    private fun refreshEurekaRegistry() {
        val client = eurekaClient ?: return
        try {
            findRefreshRegistryMethod(client.javaClass)?.let { method ->
                method.isAccessible = true
                method.invoke(client)
            }
        } catch (e: Exception) {
            log.warn("Unable to force an Eureka registry refresh, continuing with LoadBalancer cache eviction only", e)
        }
    }

    private fun findRefreshRegistryMethod(clazz: Class<*>): Method? {
        var current: Class<*>? = clazz
        while (current != null) {
            try {
                return current.getDeclaredMethod("refreshRegistry")
            } catch (e: NoSuchMethodException) {
                current = current.superclass
            }
        }
        return null
    }

}

data class DiscoveryRefreshResult(
    val evictedCaches: List<String>,
    val refreshedAt: Instant
)
