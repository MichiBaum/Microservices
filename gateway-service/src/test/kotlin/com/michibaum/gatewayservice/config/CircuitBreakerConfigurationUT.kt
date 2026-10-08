package com.michibaum.gatewayservice.config

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

    @Test
    fun `evicts load balancer cache entry when circuit breaker transitions to open`() {
        // GIVEN
        val service = Service.WEBSITE
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(service.cbId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(service.cbId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(service.cbId)).thenReturn(mock(CircuitBreaker::class.java))

        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)

        // WHEN
        createCircuitBreaker(service, circuitBreakerFactory, loadBalancerCacheManager)
        resilience4jCircuitBreaker.transitionToOpenState()

        // THEN
        verify(cache).evict(service.id)
    }

    @Test
    fun `does not evict load balancer cache entry on transitions other than open`() {
        // GIVEN
        val service = Service.WEBSITE
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(service.cbId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(service.cbId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(service.cbId)).thenReturn(mock(CircuitBreaker::class.java))

        val cache = mock(Cache::class.java)
        val loadBalancerCacheManager = mock(LoadBalancerCacheManager::class.java)
        `when`(loadBalancerCacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME))
            .thenReturn(cache)

        // WHEN
        createCircuitBreaker(service, circuitBreakerFactory, loadBalancerCacheManager)
        resilience4jCircuitBreaker.transitionToDisabledState()
        resilience4jCircuitBreaker.transitionToClosedState()

        // THEN
        verify(cache, never()).evict(service.id)
    }

    @Test
    fun `does not throw when load balancer cache manager is absent`() {
        // GIVEN
        val service = Service.WEBSITE
        val resilience4jCircuitBreaker = Resilience4jCircuitBreaker.ofDefaults(service.cbId)
        val circuitBreakerRegistry = mock(CircuitBreakerRegistry::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker(service.cbId)).thenReturn(resilience4jCircuitBreaker)

        val circuitBreakerFactory = mock(Resilience4JCircuitBreakerFactory::class.java)
        `when`(circuitBreakerFactory.circuitBreakerRegistry).thenReturn(circuitBreakerRegistry)
        `when`(circuitBreakerFactory.create(service.cbId)).thenReturn(mock(CircuitBreaker::class.java))

        // WHEN
        createCircuitBreaker(service, circuitBreakerFactory, loadBalancerCacheManager = null)

        // THEN no exception is thrown
        resilience4jCircuitBreaker.transitionToOpenState()
    }

}
