package com.michibaum.gatewayservice.config

import com.netflix.discovery.EurekaClient
import org.apache.commons.logging.LogFactory
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Operational endpoint allowing operators to clear LoadBalancer caches without restarting the
 * gateway pod. Eureka's public client API does not provide a synchronous registry refresh
 * operation, so the result reports that the registry was not refreshed and Eureka's periodic fetch
 * remains responsible for updating it. Exposed at `/actuator/discoveryRefresh` (POST).
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
        val registryRefreshed = refreshEurekaRegistry()

        val evictedCaches = loadBalancerCacheManager?.cacheNames?.toList().orEmpty()
        evictedCaches.forEach { cacheName ->
            loadBalancerCacheManager?.getCache(cacheName)?.clear()
        }

        log.info("Discovery refresh completed: registryRefreshed=$registryRefreshed, evictedCaches=$evictedCaches")

        return DiscoveryRefreshResult(
            registryRefreshed = registryRefreshed,
            evictedCaches = evictedCaches,
            refreshedAt = Instant.now()
        )
    }

    private fun refreshEurekaRegistry(): Boolean {
        if (eurekaClient == null) {
            log.warn("Unable to force an Eureka registry refresh: EurekaClient is unavailable; clearing LoadBalancer caches only")
        } else {
            log.warn("EurekaClient has no supported synchronous registry refresh API; relying on its periodic fetch and clearing LoadBalancer caches")
        }
        return false
    }

}

data class DiscoveryRefreshResult(
    val registryRefreshed: Boolean,
    val evictedCaches: List<String>,
    val refreshedAt: Instant
)
