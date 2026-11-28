package com.michibaum.gatewayservice.config

import com.netflix.discovery.EurekaClient
import org.apache.commons.logging.LogFactory
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.Selector
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Operational endpoint allowing operators to clear LoadBalancer caches and immediately synchronize
 * dynamic gateway service configurations without restarting the gateway pod. Exposed at
 * `/actuator/discoveryRefresh` (POST).
 */
@Component
@Endpoint(id = "discoveryRefresh")
class DiscoveryRefreshEndpoint(
    private val eurekaClient: EurekaClient?,
    private val loadBalancerCacheManager: LoadBalancerCacheManager?,
    private val serviceGatewayConfigStore: ServiceGatewayConfigStore
) {

    private val log = LogFactory.getLog(DiscoveryRefreshEndpoint::class.java)

    @WriteOperation
    fun refresh(): DiscoveryRefreshResult {
        return performRefresh(evictStaleServices = true)
    }

    @WriteOperation
    fun refreshWithMode(@Selector mode: String): DiscoveryRefreshResult {
        val evictStale = mode.equals("evict", ignoreCase = true) || mode.equals("purge", ignoreCase = true)
        return performRefresh(evictStaleServices = evictStale)
    }

    internal fun performRefresh(evictStaleServices: Boolean): DiscoveryRefreshResult {
        val registryRefreshed = refreshEurekaRegistry()

        val evictedCaches = loadBalancerCacheManager?.cacheNames?.toList().orEmpty()
        evictedCaches.forEach { cacheName ->
            loadBalancerCacheManager?.getCache(cacheName)?.clear()
        }

        val syncSummary = serviceGatewayConfigStore.refresh(
            evictMissingServices = evictStaleServices
        )

        log.info(
            "Discovery refresh completed: registryRefreshed=$registryRefreshed, " +
                "evictedCaches=$evictedCaches, routesUpdated=${syncSummary.updated}, " +
                "activeServices=${syncSummary.activeServices}, evictedServices=${syncSummary.evictedServices}"
        )

        return DiscoveryRefreshResult(
            registryRefreshed = registryRefreshed,
            evictedCaches = evictedCaches,
            routesUpdated = syncSummary.updated,
            activeServices = syncSummary.activeServices,
            evictedServices = syncSummary.evictedServices,
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
    val routesUpdated: Boolean = false,
    val activeServices: List<String> = emptyList(),
    val evictedServices: List<String> = emptyList(),
    val refreshedAt: Instant
)
