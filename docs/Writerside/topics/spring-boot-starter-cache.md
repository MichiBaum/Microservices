# Spring Boot Starter Cache

The `spring-boot-starter-cache` module is a custom Spring Boot starter that provides enhanced caching capabilities, specifically focusing on Time-to-Live (TTL) support for Caffeine caches and improved cache statistics.

## Overview

While Spring Boot's default caching abstraction is powerful, configuring individual TTLs for different caches when using Caffeine can be cumbersome. This starter simplifies that process by allowing TTL configuration directly through custom annotations and automatically managing the `CacheManager`.

### Key Features

- **Per-cache TTL**: Define different expiration times for each cache.
- **Cache Statistics**: Easily enable or disable statistics collection for Caffeine caches.
- **Custom Annotations**: Use `@ProjectCacheable` and `@ProjectCacheEvict` for a more integrated experience.
- **Dynamic Cache Creation**: Supports creating caches on the fly with default settings if not explicitly configured.

## Configuration

The starter automatically configures a `TtlAwareCaffeineCacheManager` bean if no other `CacheManager` is present. It scans all beans in the application context for `@ProjectCacheable` and `@ProjectCacheEvict` annotations to determine the initial cache configurations.

## Usage

### Annotations

#### @ProjectCacheable

This annotation is an extension of Spring's `@Cacheable`. It adds attributes for TTL and statistics.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `cacheNames` | `String[]` | `[]` | Names of the caches. |
| `key` | `String` | `""` | SpEL expression for computing the key. |
| `condition` | `String` | `""` | SpEL expression for conditional caching. |
| `unless` | `String` | `""` | SpEL expression to veto caching. |
| `ttl` | `Long` | `-1` | Time-to-live value. |
| `ttlUnit` | `TimeUnit` | `MINUTES` | Unit for the TTL value. |
| `statsEnabled` | `Boolean` | `true` | Whether to record statistics for this cache. |

#### @ProjectCacheEvict

An extension of Spring's `@CacheEvict`. It supports all standard attributes of `@CacheEvict`.

### Examples

#### Basic Service Usage

```kotlin
@Service
class ProductService {

    @ProjectCacheable(cacheNames = ["products"], ttl = 1, ttlUnit = TimeUnit.HOURS)
    fun getProduct(id: String): Product {
        // ... fetching logic
    }
    
    @ProjectCacheEvict(cacheNames = ["products"], key = "#product.id")
    fun updateProduct(product: Product) {
        // ... update logic
    }
}
```

#### Spring Data JPA Repositories

You can also use these annotations on repository interfaces by overriding the methods:

```kotlin
interface UserRepository : JpaRepository<User, Long> {

    @ProjectCacheable(cacheNames = ["users"], ttl = 30, ttlUnit = TimeUnit.MINUTES)
    override fun findAll(): List<User>

    @ProjectCacheEvict(cacheNames = ["users"], allEntries = true)
    override fun <S : User> save(entity: S): S
}
```

## How it Works

1. **Scanning**: On startup, `CacheAutoConfiguration` scans all beans for `ProjectCacheable` and `ProjectCacheEvict`.
2. **Metadata Gathering**: It extracts cache names, TTL values, and statistics settings.
3. **Conflict Resolution**: If multiple annotations define the same cache name with different TTLs, a warning is logged, and the latest one discovered is used.
4. **Manager Initialization**: A `TtlAwareCaffeineCacheManager` is created with the gathered configurations.
5. **Dynamic Creation**: If a cache is requested at runtime that wasn't discovered during scanning, it is created with default settings (no TTL, stats enabled).
