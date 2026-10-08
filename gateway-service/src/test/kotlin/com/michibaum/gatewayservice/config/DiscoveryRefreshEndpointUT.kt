package com.michibaum.gatewayservice.config

import com.netflix.discovery.EurekaClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.cache.Cache
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier

class DiscoveryRefreshEndpointUT {

    @Test
    fun `clears all known LoadBalancer caches and returns their names`() {
        // GIVEN
        val eurekaClient = mock(EurekaClient::class.java)
        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.cacheNames)
            .thenReturn(listOf(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        verify(cache).clear()
        assertEquals(listOf(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME), result.evictedCaches)
    }

    @Test
    fun `does not throw and returns an empty result when LoadBalancerCacheManager is absent`() {
        // GIVEN
        val eurekaClient = mock(EurekaClient::class.java)
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager = null)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
    }

    @Test
    fun `does not throw when the Eureka client is unreachable`() {
        // GIVEN - a mock Eureka client whose class has no accessible refreshRegistry method,
        // simulating an unreachable/unsupported client without crashing the gateway
        val eurekaClient = mock(EurekaClient::class.java)
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager = null)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
    }

    @Test
    fun `does not throw when the Eureka client bean is absent`() {
        // GIVEN
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient = null, loadBalancerCacheManager = null)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
    }

}
