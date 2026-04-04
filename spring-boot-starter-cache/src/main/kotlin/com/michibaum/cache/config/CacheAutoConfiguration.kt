package com.michibaum.cache.config

import com.michibaum.cache.ProjectCacheEvict
import com.michibaum.cache.ProjectCacheable
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.AnnotatedElementUtils
import java.time.Duration
import java.util.concurrent.TimeUnit

@EnableCaching
@AutoConfiguration
class CacheAutoConfiguration {
    
    private val logger = LoggerFactory.getLogger(CacheAutoConfiguration::class.java)
    
    /**
     * Configures and provides a `CacheManager` bean implementation that supports time-to-live (TTL) settings
     * for specific caches. Inspects the application context to gather caching metadata defined via the
     * `@ProjectCacheable` annotation at the class or method level.
     *
     * @param ctx The application context used to retrieve beans and their annotations to configure cache settings.
     * @return A `CacheManager` instance with TTL-aware behavior based on the specified configurations.
     */
    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(ctx: ApplicationContext): CacheManager {
        val configsByCache = mutableMapOf<String, TtlAwareCaffeineCacheManagerConfiguration>()

        ctx.beanDefinitionNames.forEach { beanName ->
            val type = ctx.getType(beanName) ?: return@forEach
            
            // Class-level
            AnnotatedElementUtils.findAllMergedAnnotations(type, ProjectCacheable::class.java).forEach { ann ->
                val ttl = toDurationOrNull(ann.ttl, ann.ttlUnit)
                val names = (ann.cacheNames + ann.value).filter { it.isNotBlank() }
                val config = TtlAwareCaffeineCacheManagerConfiguration(ttl, ann.statsEnabled)
                names.forEach { updateConfig(it, config, configsByCache) }
            }
            
            AnnotatedElementUtils.findAllMergedAnnotations(type, ProjectCacheEvict::class.java).forEach { ann ->
                val names = (ann.cacheNames + ann.value).filter { it.isNotBlank() }
                val config = TtlAwareCaffeineCacheManagerConfiguration(null, true)
                names.forEach { updateConfig(it, config, configsByCache) }
            }

            // Method-level
            type.methods.forEach { m ->
                val ann = AnnotatedElementUtils.findMergedAnnotation(m, ProjectCacheable::class.java)
                if (ann != null) {
                    val ttl = toDurationOrNull(ann.ttl, ann.ttlUnit)
                    val names = (ann.cacheNames + ann.value).filter { it.isNotBlank() }
                    val config = TtlAwareCaffeineCacheManagerConfiguration(ttl, ann.statsEnabled)
                    names.forEach { updateConfig(it, config, configsByCache) }
                }
                
                val evictAnn = AnnotatedElementUtils.findMergedAnnotation(m, ProjectCacheEvict::class.java)
                if (evictAnn != null) {
                    val names = (evictAnn.cacheNames + evictAnn.value).filter { it.isNotBlank() }
                    val config = TtlAwareCaffeineCacheManagerConfiguration(null, true)
                    names.forEach { updateConfig(it, config, configsByCache) }
                }
            }
        }

        return TtlAwareCaffeineCacheManager(configsByCache).apply {
            setCacheNames(configsByCache.keys)
            resetToDynamic()
        }
    }

    private fun updateConfig(
        cacheName: String,
        newConfig: TtlAwareCaffeineCacheManagerConfiguration,
        configsByCache: MutableMap<String, TtlAwareCaffeineCacheManagerConfiguration>
    ) {
        val existing = configsByCache[cacheName]
        if (existing == null) {
            configsByCache[cacheName] = newConfig
            return
        }

        val mergedTtl = when {
            newConfig.ttl != null && existing.ttl != null -> {
                if (newConfig.ttl != existing.ttl) {
                    logger.warn("Cache '$cacheName' has conflicting TTL values: ${existing.ttl} vs ${newConfig.ttl}. Using the new one.")
                }
                newConfig.ttl
            }
            newConfig.ttl != null -> newConfig.ttl
            else -> existing.ttl
        }
        
        val mergedStats = existing.statsEnabled || newConfig.statsEnabled
        
        configsByCache[cacheName] = TtlAwareCaffeineCacheManagerConfiguration(mergedTtl, mergedStats)
    }

    private fun toDurationOrNull(ttl: Long, unit: TimeUnit): Duration? {
        if (ttl <= 0)
            return null
        return Duration.of(ttl, unit.toChronoUnit())
    }
    
}