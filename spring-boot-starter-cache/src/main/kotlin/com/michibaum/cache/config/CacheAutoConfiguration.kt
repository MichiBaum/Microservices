package com.michibaum.cache.config

import com.michibaum.cache.ProjectCacheable
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.findAnnotations

@EnableCaching
@AutoConfiguration
class CacheAutoConfiguration {
    
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
        val ttlByCache = mutableMapOf<String, Duration>()

        ctx.beanDefinitionNames.forEach { beanName ->
            val bean = runCatching { ctx.getBean(beanName) }.getOrNull() ?: return@forEach
            val kClass = bean::class

            // Class-level
            kClass.findAnnotations<ProjectCacheable>().forEach { ann ->
                val ttl = toDurationOrNull(ann.ttl, ann.ttlUnit)
                val names = (ann.cacheNames + ann.value).filter { it.isNotBlank() }
                if (ttl != null)
                    names.forEach { ttlByCache.putIfAbsent(it, ttl) }
            }

            // Method-level
            bean::class.java.methods.forEach { m ->
                val ann = m.getAnnotation(ProjectCacheable::class.java)
                    ?: return@forEach
                val ttl = toDurationOrNull(ann.ttl, ann.ttlUnit)
                val names = (ann.cacheNames + ann.value).filter { it.isNotBlank() }
                if (ttl != null)
                    names.forEach { ttlByCache.putIfAbsent(it, ttl) }
            }
        }

        return TtlAwareCaffeineCacheManager(ttlByCache).apply {
            setCacheNames(ttlByCache.keys)
            resetToDynamic()
        }
    }

    private fun toDurationOrNull(ttl: Long, unit: TimeUnit): Duration? {
        if (ttl <= 0)
            return null
        return when (unit) {
            TimeUnit.NANOSECONDS -> throw UnsupportedOperationException("Nanoseconds")
            TimeUnit.MICROSECONDS -> throw UnsupportedOperationException("Microseconds")
            TimeUnit.MILLISECONDS -> throw UnsupportedOperationException("Milliseconds")
            TimeUnit.SECONDS -> Duration.ofSeconds(ttl)
            TimeUnit.MINUTES -> Duration.ofMinutes(ttl)
            TimeUnit.HOURS -> Duration.ofHours(ttl)
            TimeUnit.DAYS -> Duration.ofDays(ttl)
        }
    }
    
}