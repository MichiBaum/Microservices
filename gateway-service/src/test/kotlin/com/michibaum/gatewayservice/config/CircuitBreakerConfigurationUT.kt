package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.CircuitBreakerMetadata
import io.github.resilience4j.circuitbreaker.CircuitBreaker as Resilience4jCircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.cache.Cache
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier

class CircuitBreakerConfigurationUT {

    private val websiteTarget = RouteTarget(
        serviceId = "website-service",
        circuitBreakerId = "website-service-circuit-breaker"
    )

    @Test
    fun `evicts load balancer cache entry when circuit breaker transitions to open`() {
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(websiteTarget.circuitBreakerId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(websiteTarget.circuitBreakerId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(websiteTarget.circuitBreakerId)).thenReturn(mock(CircuitBreaker::class.java))

        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)

        createCircuitBreaker(websiteTarget, circuitBreakerFactory, loadBalancerCacheManager)
        resilience4jCircuitBreaker.transitionToOpenState()

        verify(cache).evict(websiteTarget.serviceId)
    }

    @Test
    fun `registers only one cache eviction listener for repeated circuit breaker creation`() {
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(websiteTarget.circuitBreakerId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(websiteTarget.circuitBreakerId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(websiteTarget.circuitBreakerId)).thenReturn(mock(CircuitBreaker::class.java))

        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)

        createCircuitBreaker(websiteTarget, circuitBreakerFactory, loadBalancerCacheManager)
        createCircuitBreaker(websiteTarget, circuitBreakerFactory, loadBalancerCacheManager)
        resilience4jCircuitBreaker.transitionToOpenState()

        verify(cache).evict(websiteTarget.serviceId)
    }

    @Test
    fun `does not evict load balancer cache entry on transitions other than open`() {
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(websiteTarget.circuitBreakerId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(websiteTarget.circuitBreakerId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(websiteTarget.circuitBreakerId)).thenReturn(mock(CircuitBreaker::class.java))

        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)

        createCircuitBreaker(websiteTarget, circuitBreakerFactory, loadBalancerCacheManager)
        resilience4jCircuitBreaker.transitionToDisabledState()
        resilience4jCircuitBreaker.transitionToClosedState()

        verify(cache, never()).evict(websiteTarget.serviceId)
    }

    @Test
    fun `does not throw when load balancer cache manager is absent`() {
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(websiteTarget.circuitBreakerId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(websiteTarget.circuitBreakerId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(websiteTarget.circuitBreakerId)).thenReturn(mock(CircuitBreaker::class.java))

        createCircuitBreaker(websiteTarget, circuitBreakerFactory, loadBalancerCacheManager = null)

        resilience4jCircuitBreaker.transitionToOpenState()
    }

    @Test
    fun `defaultCustomizer configures Resilience4JCircuitBreakerFactory`() {
        val configuration = CircuitBreakerConfiguration()
        val customizer = configuration.defaultCustomizer()
        val factory = mock(Resilience4JCircuitBreakerFactory::class.java)

        customizer.customize(factory)

        verify(factory).configureDefault(org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun `applies custom CircuitBreakerMetadata overrides when present on RouteTarget`() {
        val customTarget = RouteTarget(
            serviceId = "chess-service",
            circuitBreakerId = "chess-service-circuit-breaker",
            circuitBreakerConfig = CircuitBreakerMetadata(timeoutSeconds = 25, slidingWindowSize = 80)
        )
        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.create(customTarget.circuitBreakerId)).thenReturn(mock(CircuitBreaker::class.java))

        createCircuitBreaker(customTarget, circuitBreakerFactory, loadBalancerCacheManager = null)

        verify(circuitBreakerFactory).configure(
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.eq(customTarget.circuitBreakerId)
        )
    }

}
