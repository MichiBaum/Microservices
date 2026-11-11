---
name: performance-engineer
description: Expert in query optimization, caching strategies, and performance tuning for Spring Boot microservices
---

# Performance Engineer

Use this skill when optimizing application performance, tuning queries, implementing caching, or profiling Spring Boot microservices.

## When to Use

- Identifying and fixing slow database queries
- Implementing caching strategies
- Optimizing Spring Boot application startup
- Reducing memory footprint
- Improving API response times
- Analyzing performance bottlenecks

## Performance Optimization Checklist

### Database Performance
- [ ] Use EXPLAIN to analyze query execution plans
- [ ] Add appropriate indexes on frequently queried columns
- [ ] Avoid N+1 queries using JOIN FETCH or entity graphs
- [ ] Use connection pooling (HikariCP) with appropriate settings
- [ ] Batch database operations when possible
- [ ] Use pagination for large result sets

### Caching Strategy

#### Spring Cache Implementation
- [ ] Use `@Cacheable`, `@CachePut`, `@CacheEvict` annotations
- [ ] Configure cache managers per service
- [ ] Choose appropriate cache type (Redis, Caffeine, Hazelcast)
- [ ] Set appropriate cache expiration times
- [ ] Implement cache invalidation strategies

### Application Performance

#### Startup Optimization
- [ ] Lazy initialize beans where possible
- [ ] Use `@Lazy` for self-referencing beans
- [ ] Configure appropriate connection pool sizes
- [ ] Enable Spring Boot actuator health endpoints

## Caching Patterns

### Read-Through Cache
```kotlin
@Cacheable(value = ["users"], key = "#id")
fun getUserById(id: Long): User {
    return userRepository.findById(id).orElseThrow()
}
```

### Write-Through Cache
```kotlin
@CacheEvict(value = ["users"], allEntries = false)
fun updateUser(id: Long, user: User) {
    userRepository.save(user)
}
```

## Query Optimization

### Avoid N+1 Queries
```kotlin
// BAD: Causes N+1 query problem
val users = userRepository.findAll()
users.forEach { print(it.orders.size) }

// GOOD: Uses JOIN FETCH
val usersWithOrders = userRepository.findAllByOrderByUsername()
    .joinFetch("orders")
```

### Use `@Query` with JPQL
```kotlin
@Query("SELECT u FROM User u WHERE u.email LIKE %:email")
fun findUsersByEmailContaining(email: String): List<User>
```

## Memory Optimization

### Reduce Object Creation
- Use `@Value` for configuration instead of `@ConfigurationProperties`
- Reuse immutable objects where possible
- Use `ArrayList` instead of `LinkedList`
- Prefer primitive collections for large datasets

### Connection Pool Configuration
```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Output Standards

- Provide specific performance metrics before and after optimization
- Include exact code changes with file paths
- Explain the performance impact of each change
- Suggest monitoring metrics to track improvements

## Tools to Use

### Profiling
- Spring Boot Actuator (`/actuator/metrics`, `/actuator/heapdump`)
- JProfiler or YourKit for deep profiling
- Arthas for Java diagnostics

### Monitoring
- Prometheus + Grafana for metrics
- Jaeger for distributed tracing
- Actuator health checks

## Project-Specific Notes

- All services use MariaDB for persistence
- Flyway handles schema migrations
- Use `@Cacheable` with Caffeine for local caching
- Redis recommended for distributed caching
- Enable Spring Boot Actuator for performance monitoring