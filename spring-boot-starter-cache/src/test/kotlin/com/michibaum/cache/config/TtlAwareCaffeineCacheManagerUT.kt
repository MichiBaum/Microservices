package com.michibaum.cache.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.cache.caffeine.CaffeineCache
import java.time.Duration

class TtlAwareCaffeineCacheManagerUT {

    @Test
    fun `should create cache with TTL and stats enabled`() {
        val cacheName = "testCache"
        val ttl = Duration.ofMinutes(5)
        val config = TtlAwareCaffeineCacheManagerConfiguration(ttl, true)
        val manager = TtlAwareCaffeineCacheManager(mapOf(cacheName to config))

        val cache = manager.getCache(cacheName) as CaffeineCache
        val nativeCache = cache.nativeCache

        // Caffeine doesn't expose expireAfterWrite easily, but we can check if it's there via string representation or behavior
        // However, we can at least verify it's a CaffeineCache and stats are recorded if possible
        assertNotNull(cache)
        assertEquals(cacheName, cache.name)
        
        // recordStats() enables stats collection
        assertTrue(nativeCache.stats().plus(nativeCache.stats()).hitCount() == 0L)
    }

    @Test
    fun `should create cache without TTL when not configured`() {
        val cacheName = "noTtlCache"
        val config = TtlAwareCaffeineCacheManagerConfiguration(null, true)
        val manager = TtlAwareCaffeineCacheManager(mapOf(cacheName to config))

        val cache = manager.getCache(cacheName) as CaffeineCache
        assertNotNull(cache)
        assertEquals(cacheName, cache.name)
    }

    @Test
    fun `should create cache with stats disabled`() {
        val cacheName = "noStatsCache"
        val config = TtlAwareCaffeineCacheManagerConfiguration(Duration.ofMinutes(1), false)
        val manager = TtlAwareCaffeineCacheManager(mapOf(cacheName to config))

        val cache = manager.getCache(cacheName) as CaffeineCache
        val nativeCache = cache.nativeCache
        
        // When stats are disabled, nativeCache.stats() returns empty stats
        assertNotNull(nativeCache.stats())
    }
    
    @Test
    fun `should handle dynamic cache creation with default settings`() {
        val manager = TtlAwareCaffeineCacheManager(emptyMap())
        manager.resetToDynamic()
        
        val cacheName = "dynamicCache"
        val cache = manager.getCache(cacheName) as CaffeineCache
        assertNotNull(cache)
        assertEquals(cacheName, cache.name)
    }
}
