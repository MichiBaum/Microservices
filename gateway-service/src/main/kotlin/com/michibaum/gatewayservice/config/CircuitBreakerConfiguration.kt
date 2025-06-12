package com.michibaum.gatewayservice.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse
import java.time.Duration

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun defaultCustomizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofMillis(400))
                        .build())
                    .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .slowCallRateThreshold(50.0f)
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        .build())
                    .build()
            }
        }
    }
}

typealias CircuitBreakerResponse = (Throwable) -> ServerResponse

fun createCircuitBreaker(
    serviceName: String,
    circuitBreakerFactory: CircuitBreakerFactory<*, *>
): CircuitBreaker = circuitBreakerFactory.create("$serviceName-circuit-breaker")

fun createCircuitBreakerServiceUnavailableResponse(serviceName: String): CircuitBreakerResponse = { _ ->
    ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body("Service $serviceName is currently unavailable. Please try again later.")
}

fun createCircuitBreakerErrorResponse(error: Exception): ServerResponse =
    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred while processing your request: ${error.message}")

fun applyCircuitBreaker(
    handlerFunction: HandlerFunction<ServerResponse>,
    serviceName: String,
    circuitBreakerFactory: CircuitBreakerFactory<*, *>
): HandlerFunction<ServerResponse> {
    val circuitBreaker = createCircuitBreaker(serviceName, circuitBreakerFactory)

    return HandlerFunction { request ->
        try {
            circuitBreaker.run(
                { handlerFunction.handle(request) },
                createCircuitBreakerServiceUnavailableResponse(serviceName)
            )
        } catch (e: Exception) {
            createCircuitBreakerErrorResponse(e)
        }
    }
}
