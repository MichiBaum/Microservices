package com.michibaum.gatewayservice.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cloud.client.discovery.event.HeartbeatEvent
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Event listener that invalidates Spring Cloud LoadBalancer caches whenever
 * a Eureka registry update ([HeartbeatEvent]) is received.
 *
 * This prevents gateway routing calls from serving stale/empty cached instance
 * lists when instances are registered or recovered in Eureka.
 */
@Component
class EurekaHeartbeatEventListener(
    private val cacheManagerProvider: ObjectProvider<LoadBalancerCacheManager>
) {

    private val logger = LoggerFactory.getLogger(EurekaHeartbeatEventListener::class.java)

    @EventListener
    fun onHeartbeat(event: HeartbeatEvent) {
        val cacheManager = cacheManagerProvider.ifAvailable ?: return
        val cacheNames = cacheManager.cacheNames
        if (cacheNames.isEmpty()) {
            return
        }

        logger.debug("Received HeartbeatEvent value {}, clearing {} LoadBalancer caches", event.value, cacheNames.size)
        cacheNames.forEach { cacheName ->
            cacheManager.getCache(cacheName)?.clear()
        }
    }
}
