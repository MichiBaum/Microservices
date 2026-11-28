package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.CircuitBreakerMetadata
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State
import io.github.resilience4j.circuitbreaker.CircuitBreaker as Resilience4jCircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse
import java.time.Duration
import java.util.Collections
import java.util.WeakHashMap

private val log = LogFactory.getLog(CircuitBreakerConfiguration::class.java)

data class RouteTarget(
    val serviceId: String,
    val circuitBreakerId: String = "$serviceId-circuit-breaker",
    val circuitBreakerConfig: CircuitBreakerMetadata? = null
)

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun defaultCustomizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(
                        TimeLimiterConfig.custom()
                            .timeoutDuration(Duration.ofSeconds(10))
                            .build()
                    )
                    .circuitBreakerConfig(
                        CircuitBreakerConfig.custom()
                            .slidingWindowSize(50)
                            .minimumNumberOfCalls(20)
                            .failureRateThreshold(50.0f)
                            .waitDurationInOpenState(Duration.ofSeconds(10))
                            .permittedNumberOfCallsInHalfOpenState(10)
                            .slowCallRateThreshold(50.0f)
                            .slowCallDurationThreshold(Duration.ofSeconds(5))
                            .build()
                    )
                    .build()
            }
        }
    }
}

private val registeredCircuitBreakers = Collections.synchronizedSet(
    Collections.newSetFromMap(WeakHashMap<Resilience4jCircuitBreaker, Boolean>())
)

typealias CircuitBreakerResponse = (Throwable?) -> ServerResponse

fun createCircuitBreaker(
    routeTarget: RouteTarget,
    circuitBreakerFactory: Resilience4JCircuitBreakerFactory,
    loadBalancerCacheManager: LoadBalancerCacheManager? = null
): CircuitBreaker {
    routeTarget.circuitBreakerConfig?.let { custom ->
        circuitBreakerFactory.configure({ builder ->
            builder
                .timeLimiterConfig(
                    TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(custom.timeoutSeconds))
                        .build()
                )
                .circuitBreakerConfig(
                    CircuitBreakerConfig.custom()
                        .slidingWindowSize(custom.slidingWindowSize)
                        .minimumNumberOfCalls(custom.minimumNumberOfCalls)
                        .failureRateThreshold(custom.failureRateThreshold)
                        .waitDurationInOpenState(Duration.ofSeconds(custom.waitDurationInOpenStateSeconds))
                        .permittedNumberOfCallsInHalfOpenState(custom.permittedNumberOfCallsInHalfOpenState)
                        .slowCallRateThreshold(custom.slowCallRateThreshold)
                        .slowCallDurationThreshold(Duration.ofSeconds(custom.slowCallDurationThresholdSeconds))
                        .build()
                )
        }, routeTarget.circuitBreakerId)
    }

    val circuitBreaker = circuitBreakerFactory.create(routeTarget.circuitBreakerId)

    if (loadBalancerCacheManager != null) {
        val resilience4jCircuitBreaker = circuitBreakerFactory.circuitBreakerRegistry
            .circuitBreaker(routeTarget.circuitBreakerId)
        synchronized(registeredCircuitBreakers) {
            if (registeredCircuitBreakers.add(resilience4jCircuitBreaker)) {
                resilience4jCircuitBreaker.eventPublisher
                    .onStateTransition { event ->
                        if (event.stateTransition.toState == State.OPEN) {
                            val defaultCache = loadBalancerCacheManager
                                .getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME)
                            defaultCache?.evict(routeTarget.serviceId)
                            loadBalancerCacheManager.getCache(routeTarget.serviceId)?.clear()
                        }
                    }
            }
        }
    }

    return circuitBreaker
}

fun createCircuitBreakerServiceUnavailableResponse(routeTarget: RouteTarget): CircuitBreakerResponse = { throwable ->
    if (throwable != null) {
        log.warn("Circuit breaker fallback triggered for service ${routeTarget.serviceId}: ${throwable.message}", throwable)
    } else {
        log.warn("Circuit breaker fallback triggered for service ${routeTarget.serviceId}")
    }
    ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body("Service ${routeTarget.serviceId} is currently unavailable. Please try again later.")
}

fun createCircuitBreakerErrorResponse(error: Exception): ServerResponse =
    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred while processing your request: ${error.message}")

fun applyCircuitBreaker(
    handlerFunction: HandlerFunction<ServerResponse>,
    routeTarget: RouteTarget,
    circuitBreakerFactory: Resilience4JCircuitBreakerFactory,
    loadBalancerCacheManager: LoadBalancerCacheManager? = null
): HandlerFunction<ServerResponse> {
    val circuitBreaker = createCircuitBreaker(routeTarget, circuitBreakerFactory, loadBalancerCacheManager)

    return HandlerFunction { request ->
        try {
            circuitBreaker.run(
                { handlerFunction.handle(request) },
                createCircuitBreakerServiceUnavailableResponse(routeTarget)
            )
        } catch (e: Exception) {
            log.error("Unexpected exception in circuit breaker handler for service ${routeTarget.serviceId}", e)
            createCircuitBreakerErrorResponse(e)
        }
    }
}
