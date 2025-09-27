package com.michibaum.cache.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean

@EnableCaching
@AutoConfiguration
class CacheAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    fun cacheManager(): CacheManager {
        return CaffeineCacheManager()
    }
    
}