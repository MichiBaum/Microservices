package com.michibaum.cache

import org.springframework.cache.annotation.Cacheable
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Cacheable
annotation class ProjectCacheable(

    @get:AliasFor(annotation = Cacheable::class, attribute = "cacheNames")
    val cacheNames: Array<String> = [],

    @get:AliasFor(annotation = Cacheable::class, attribute = "value")
    val value: Array<String> = [],

    @get:AliasFor(annotation = Cacheable::class, attribute = "key")
    val key: String = "",

    @get:AliasFor(annotation = Cacheable::class, attribute = "condition")
    val condition: String = "",

    @get:AliasFor(annotation = Cacheable::class, attribute = "unless")
    val unless: String = ""
    
)