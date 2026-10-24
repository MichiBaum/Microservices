package com.michibaum.authentication_starter

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.security.jwt.JwsValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import java.time.Duration
import java.util.function.Consumer

class AuthenticationAutoConfigurationUT {

    @Test
    fun `jwsValidator bean is created properly`() {
        val configuration = AuthenticationAutoConfiguration()
        val authenticationClient = mock(AuthenticationClient::class.java)

        val jwsValidator: JwsValidator = configuration.jwsValidator(authenticationClient, null)

        assertNotNull(jwsValidator)
    }

    @Test
    fun `authenticationPublicKeyCircuitBreakerCustomizer configures 5 second timeout for public key circuit breaker`() {
        val configuration = AuthenticationAutoConfiguration()
        val customizer = configuration.authenticationPublicKeyCircuitBreakerCustomizer()

        val factory = mock(Resilience4JCircuitBreakerFactory::class.java)
        customizer.customize(factory)

        @Suppress("UNCHECKED_CAST")
        val captor = ArgumentCaptor.forClass(Consumer::class.java) as ArgumentCaptor<Consumer<Resilience4JConfigBuilder>>
        verify(factory).configure(captor.capture(), org.mockito.ArgumentMatchers.eq("authentication-service-public-key"))

        val builder = Resilience4JConfigBuilder("authentication-service-public-key")
        captor.value.accept(builder)

        val config = builder.build()
        assertEquals(Duration.ofSeconds(5), config.timeLimiterConfig?.timeoutDuration)
        assertEquals(10, config.circuitBreakerConfig?.slidingWindowSize)
        assertEquals(50.0f, config.circuitBreakerConfig?.failureRateThreshold)
        assertEquals(30000L, config.circuitBreakerConfig?.waitIntervalFunctionInOpenState?.apply(1))
    }
}
