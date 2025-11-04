package com.michibaum.cache.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import java.time.Duration

/**
 * A specialized implementation of `CaffeineCacheManager` that supports per-cache time-to-live (TTL) configurations.
 * The TTL configuration is defined utilizing a map of cache names to TTL durations, allowing specific expiration
 * policies for different caches.
 *
 * This class facilitates the creation and management of Caffeine caches with optional expiration policies, providing
 * flexibility to configure cache behaviors dynamically based on given requirements.
 *
 * @constructor
 * @param ttlByCacheName A map where the keys are cache names and the values are corresponding TTL durations.
 */
class TtlAwareCaffeineCacheManager (
    private val ttlByCacheName: Map<String, Duration>
) : CaffeineCacheManager() {

    /**
     * Creates a new cache instance with the specified name using Caffeine as the underlying cache implementation.
     * If a time-to-live (TTL) duration is defined for the given cache name in the `ttlByCacheName` map and is positive,
     * the cache will expire entries after the defined duration.
     *
     * @param name The name of the cache to be created.
     * @return A Caffeine-based cache instance configured with the specified name and TTL if applicable.
     */
    override fun createCaffeineCache(name: String): Cache {
        val builder = Caffeine.newBuilder()
            .recordStats() // TODO define in annotation that stats are enabled by default but can be disabled
        val ttl = ttlByCacheName[name]
        if (ttl != null && !ttl.isZero && !ttl.isNegative) {
            builder.expireAfterWrite(ttl)
        }
        return CaffeineCache(name, builder.build<Any, Any>())
    }
    
    /**
     * Resets the cache manager to dynamic mode by removing predefined cache names.
     * This allows the cache manager to dynamically create caches as needed without
     * being constrained to a fixed set of cache names.
     */
    fun resetToDynamic(){
        this.setCacheNames(null)
    }
    
}