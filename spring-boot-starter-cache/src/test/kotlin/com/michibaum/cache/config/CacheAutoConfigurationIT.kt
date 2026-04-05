package com.michibaum.cache.config

import com.michibaum.cache.ProjectCacheEvict
import com.michibaum.cache.ProjectCacheable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.ApplicationContext
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [CacheAutoConfigurationIT.TestApp::class])
class CacheAutoConfigurationIT {

    @Autowired
    lateinit var cacheManager: CacheManager

    @Autowired
    lateinit var ctx: ApplicationContext

    @Test
    fun `should configure cacheManager with TTL and stats`() {
        assertNotNull(cacheManager)
        assertTrue(cacheManager is TtlAwareCaffeineCacheManager)
        
        val ttlCache = cacheManager.getCache("ttlCache") as CaffeineCache
        val noTtlCache = cacheManager.getCache("noTtlCache") as CaffeineCache
        val classLevelCache = cacheManager.getCache("classLevelCache") as CaffeineCache
        val evictCache = cacheManager.getCache("evictCache") as CaffeineCache
        val aliasCache = cacheManager.getCache("aliasCache") as CaffeineCache
        val conflictingCache = cacheManager.getCache("conflictingCache") as CaffeineCache
        
        assertNotNull(ttlCache)
        assertNotNull(noTtlCache)
        assertNotNull(classLevelCache)
        assertNotNull(evictCache)
        assertNotNull(aliasCache)
        assertNotNull(conflictingCache)
    }

    @SpringBootApplication
    @EnableCaching
    class TestApp {
        
        @Service
        @ProjectCacheable(cacheNames = ["classLevelCache"], ttl = 10, ttlUnit = TimeUnit.MINUTES)
        class ClassLevelService {
            @Cacheable("methodOverrideCache")
            fun cachedMethod() = "value"
        }

        @Service
        class MethodLevelService {
            
            @ProjectCacheable(cacheNames = ["ttlCache"], ttl = 5, ttlUnit = TimeUnit.SECONDS)
            fun ttlMethod() = "ttl"
            
            @ProjectCacheable(cacheNames = ["noTtlCache"])
            fun noTtlMethod() = "noTtl"
            
            @ProjectCacheEvict(cacheNames = ["evictCache"])
            fun evictMethod() {}

            @ProjectCacheable(value = ["aliasCache"], ttl = 1, ttlUnit = TimeUnit.HOURS)
            fun aliasMethod() = "alias"

            @ProjectCacheable(cacheNames = ["conflictingCache"], ttl = 10)
            fun conflict1() = "c1"

            @ProjectCacheable(cacheNames = ["conflictingCache"], ttl = 20)
            fun conflict2() = "c2"
        }
    }
}
