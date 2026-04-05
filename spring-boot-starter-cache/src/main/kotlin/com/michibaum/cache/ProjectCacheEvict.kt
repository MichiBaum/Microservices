package com.michibaum.cache

import org.springframework.cache.annotation.CacheEvict
import org.springframework.core.annotation.AliasFor

/**
 * Annotation to enable cache eviction for methods or classes.
 * It works as an extension of the Spring's `@CacheEvict` annotation.
 *
 * @property cacheNames Assigns a list of cache names from which the entries will be evicted.
 *           Works synonymously with the `value` attribute of the `@CacheEvict` annotation.
 * @property value An alias for `cacheNames`.
 * @property key Defines the cache key to use for identifying the entry to evict, supporting SpEL.
 * @property condition Specifies the eviction condition using SpEL. Eviction is applied only if the condition evaluates to true.
 * @property allEntries Whether all entries within the cache(s) should be removed.
 * @property beforeInvocation Whether the eviction should occur before the method is invoked.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@CacheEvict
annotation class ProjectCacheEvict(

    @get:AliasFor(annotation = CacheEvict::class, attribute = "cacheNames")
    val cacheNames: Array<String> = [],

    @get:AliasFor(annotation = CacheEvict::class, attribute = "value")
    val value: Array<String> = [],

    @get:AliasFor(annotation = CacheEvict::class, attribute = "key")
    val key: String = "",

    @get:AliasFor(annotation = CacheEvict::class, attribute = "condition")
    val condition: String = "",

    @get:AliasFor(annotation = CacheEvict::class, attribute = "allEntries")
    val allEntries: Boolean = false,

    @get:AliasFor(annotation = CacheEvict::class, attribute = "beforeInvocation")
    val beforeInvocation: Boolean = false
)
