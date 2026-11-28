package com.michibaum.gatewayservice.config

import com.netflix.discovery.EurekaClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.cache.Cache
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier

class DiscoveryRefreshEndpointUT {

    private val emptyStore = ServiceGatewayConfigStore(discoveryClient = null)

    @Test
    fun `clears all known LoadBalancer caches and syncs ServiceGatewayConfigStore`() {
        // GIVEN
        val eurekaClient = mock(EurekaClient::class.java)
        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.cacheNames)
            .thenReturn(listOf(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)
        val store = mock(ServiceGatewayConfigStore::class.java)
        `when`(store.refresh(true)).thenReturn(
            GatewayDiscoverySyncSummary(
                updated = true,
                activeServices = listOf("chess-service"),
                evictedServices = listOf("old-service")
            )
        )
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager, store)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        verify(cache).clear()
        verify(store).refresh(true)
        assertFalse(result.registryRefreshed)
        assertTrue(result.routesUpdated)
        assertEquals(listOf("chess-service"), result.activeServices)
        assertEquals(listOf("old-service"), result.evictedServices)
        assertEquals(listOf(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME), result.evictedCaches)
    }

    @Test
    fun `refreshWithMode sync retains last-known-good snapshots`() {
        val store = mock(ServiceGatewayConfigStore::class.java)
        `when`(store.refresh(false)).thenReturn(
            GatewayDiscoverySyncSummary(
                updated = false,
                activeServices = listOf("chess-service"),
                evictedServices = emptyList()
            )
        )
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient = null, loadBalancerCacheManager = null, serviceGatewayConfigStore = store)

        val result = endpoint.refreshWithMode("sync")

        verify(store).refresh(false)
        assertEquals(listOf("chess-service"), result.activeServices)
    }

    @Test
    fun `does not throw and returns an empty result when LoadBalancerCacheManager is absent`() {
        // GIVEN
        val eurekaClient = mock(EurekaClient::class.java)
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager = null, serviceGatewayConfigStore = emptyStore)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
    }

    @Test
    fun `reports that registry was not refreshed when Eureka client is present`() {
        // GIVEN
        val eurekaClient = mock(EurekaClient::class.java)
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient, loadBalancerCacheManager = null, serviceGatewayConfigStore = emptyStore)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
        assertFalse(result.registryRefreshed)
    }

    @Test
    fun `does not throw when the Eureka client bean is absent`() {
        // GIVEN
        val endpoint = DiscoveryRefreshEndpoint(eurekaClient = null, loadBalancerCacheManager = null, serviceGatewayConfigStore = emptyStore)

        // WHEN
        val result = endpoint.refresh()

        // THEN
        assertTrue(result.evictedCaches.isEmpty())
        assertFalse(result.registryRefreshed)
    }

}
