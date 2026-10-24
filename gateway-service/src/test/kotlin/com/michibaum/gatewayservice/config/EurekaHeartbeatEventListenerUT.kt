package com.michibaum.gatewayservice.config

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cache.Cache
import org.springframework.cloud.client.discovery.event.HeartbeatEvent
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager

class EurekaHeartbeatEventListenerUT {

    @Test
    fun `onHeartbeat clears all caches when cacheManager is available`() {
        @Suppress("UNCHECKED_CAST")
        val cacheManagerProvider = mock(ObjectProvider::class.java) as ObjectProvider<LoadBalancerCacheManager>
        val cacheManager = mock(LoadBalancerCacheManager::class.java)
        val musicCache = mock(Cache::class.java)
        val chessCache = mock(Cache::class.java)

        `when`(cacheManagerProvider.ifAvailable).thenReturn(cacheManager)
        `when`(cacheManager.cacheNames).thenReturn(listOf("music-service", "chess-service"))
        `when`(cacheManager.getCache("music-service")).thenReturn(musicCache)
        `when`(cacheManager.getCache("chess-service")).thenReturn(chessCache)

        val listener = EurekaHeartbeatEventListener(cacheManagerProvider)
        val event = HeartbeatEvent("source", "value")

        listener.onHeartbeat(event)

        verify(musicCache).clear()
        verify(chessCache).clear()
    }

    @Test
    fun `onHeartbeat does nothing when cacheManager is not available`() {
        @Suppress("UNCHECKED_CAST")
        val cacheManagerProvider = mock(ObjectProvider::class.java) as ObjectProvider<LoadBalancerCacheManager>
        `when`(cacheManagerProvider.ifAvailable).thenReturn(null)

        val listener = EurekaHeartbeatEventListener(cacheManagerProvider)
        val event = HeartbeatEvent("source", "value")

        listener.onHeartbeat(event)

        verify(cacheManagerProvider).ifAvailable
    }

    @Test
    fun `onHeartbeat handles empty cache names gracefully`() {
        @Suppress("UNCHECKED_CAST")
        val cacheManagerProvider = mock(ObjectProvider::class.java) as ObjectProvider<LoadBalancerCacheManager>
        val cacheManager = mock(LoadBalancerCacheManager::class.java)

        `when`(cacheManagerProvider.ifAvailable).thenReturn(cacheManager)
        `when`(cacheManager.cacheNames).thenReturn(emptyList())

        val listener = EurekaHeartbeatEventListener(cacheManagerProvider)
        val event = HeartbeatEvent("source", "value")

        listener.onHeartbeat(event)

        verify(cacheManager).cacheNames
    }
}
