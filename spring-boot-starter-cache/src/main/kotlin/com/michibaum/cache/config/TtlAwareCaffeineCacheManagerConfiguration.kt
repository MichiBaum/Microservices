package com.michibaum.cache.config

import java.time.Duration

data class TtlAwareCaffeineCacheManagerConfiguration(
    val ttl: Duration,
    val statsEnabled: Boolean
)
