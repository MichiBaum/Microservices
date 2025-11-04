package com.michibaum.cache

import org.springframework.cache.annotation.Cacheable
import org.springframework.core.annotation.AliasFor
import java.util.concurrent.TimeUnit

/**
 * Annotation to enable caching for methods or classes with additional control over time-to-live (TTL) properties.
 * It works as an extension of the Spring's `@Cacheable` annotation, providing the ability to define a cache's time-to-live duration.
 * The TTL is configurable via the `ttl` and `ttlUnit` attributes and is managed by a compatible cache manager.
 *
 * When this annotation is used, caching behavior is applied to the annotated method's or class's output.
 * The results are stored in the cache specified by `cacheNames` or `value`, and can be conditionally cached
 * based on provided `key`, `condition`, or `unless` attributes.
 *
 * @property cacheNames Assigns a list of cache names to where the results will be stored.
 *           Works synonymously with the `value` attribute of the `@Cacheable` annotation.
 * @property value An alias for `cacheNames`. If both `cacheNames` and `value` are defined, they are considered equivalent.
 * @property key Defines the cache key to use for the cache entry, supporting Spring Expression Language (SpEL).
 * @property condition Specifies the caching condition using SpEL. Caching is applied only if the condition evaluates to true.
 * @property unless Specifies a SpEL condition to veto the caching operation. Caching is skipped if the condition evaluates to true.
 * @property ttl Specifies the time-to-live duration for the cache entry. Defaults to -1, which indicates no TTL setting.
 * @property ttlUnit Specifies the time unit of the `ttl` value. Defaults to `TimeUnit.MINUTES`.
 */
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
    val unless: String = "",

    val ttl: Long = -1,
    val ttlUnit: TimeUnit = TimeUnit.MINUTES,
    
    val statsEnabled: Boolean = true
)