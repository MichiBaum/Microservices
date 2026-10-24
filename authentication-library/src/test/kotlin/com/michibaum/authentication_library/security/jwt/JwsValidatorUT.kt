package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker as Resilience4jCircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import java.security.KeyPairGenerator
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Function
import java.util.function.Supplier

class JwsValidatorUT {

    private lateinit var authenticationClient: AuthenticationClient
    private lateinit var circuitBreakerFactory: CircuitBreakerFactory<*, *>
    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var validPublicKeyDto: PublicKeyDto

    @BeforeEach
    fun setUp() {
        authenticationClient = mock(AuthenticationClient::class.java)
        circuitBreakerFactory = mock(CircuitBreakerFactory::class.java)
        circuitBreaker = mock(CircuitBreaker::class.java)
        `when`(circuitBreakerFactory.create("authentication-service-public-key")).thenReturn(circuitBreaker)

        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(2048)
        val keyPair = keyPairGen.generateKeyPair()
        validPublicKeyDto = PublicKeyDto("RSA", keyPair.public.encoded)
    }

    @Test
    fun `reloadPublicKey successfully updates key without circuit breaker`() {
        `when`(authenticationClient.publicKey()).thenReturn(validPublicKeyDto)

        val validator = JwsValidator(authenticationClient)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `reloadPublicKey successfully updates key with circuit breaker`() {
        `when`(circuitBreaker.run(any<Supplier<PublicKeyDto>>(), any<Function<Throwable?, PublicKeyDto>>()))
            .thenAnswer { invocation ->
                val supplier = invocation.getArgument<Supplier<PublicKeyDto>>(0)
                supplier.get()
            }
        `when`(authenticationClient.publicKey()).thenReturn(validPublicKeyDto)

        val validator = JwsValidator(authenticationClient, circuitBreakerFactory)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `creates public key circuit breaker lazily after validator construction`() {
        `when`(circuitBreaker.run(any<Supplier<PublicKeyDto>>(), any<Function<Throwable?, PublicKeyDto>>()))
            .thenAnswer { invocation ->
                val supplier = invocation.getArgument<Supplier<PublicKeyDto>>(0)
                supplier.get()
            }
        `when`(authenticationClient.publicKey()).thenReturn(validPublicKeyDto)

        val validator = JwsValidator(authenticationClient, circuitBreakerFactory)
        verify(circuitBreakerFactory, never()).create("authentication-service-public-key")

        validator.reloadPublicKey()
        validator.reloadPublicKey()

        verify(circuitBreakerFactory, times(1)).create("authentication-service-public-key")
    }

    @Test
    fun `reloadPublicKey handles timeout exception gracefully without re-throwing`() {
        `when`(circuitBreaker.run(any<Supplier<PublicKeyDto>>(), any<Function<Throwable?, PublicKeyDto>>()))
            .thenAnswer { invocation ->
                val fallback = invocation.getArgument<Function<Throwable?, PublicKeyDto?>>(1)
                fallback.apply(TimeoutException("TimeLimiter 'authentication-service-public-key' recorded a timeout exception."))
            }

        val validator = JwsValidator(authenticationClient, circuitBreakerFactory)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `reloadPublicKey handles call not permitted exception when circuit breaker is open`() {
        val r4jCircuitBreaker = Resilience4jCircuitBreaker.of("authentication-service-public-key", CircuitBreakerConfig.ofDefaults())
        val callNotPermittedException = CallNotPermittedException.createCallNotPermittedException(r4jCircuitBreaker)

        `when`(circuitBreaker.run(any<Supplier<PublicKeyDto>>(), any<Function<Throwable?, PublicKeyDto>>()))
            .thenAnswer { invocation ->
                val fallback = invocation.getArgument<Function<Throwable?, PublicKeyDto?>>(1)
                fallback.apply(callNotPermittedException)
            }

        val validator = JwsValidator(authenticationClient, circuitBreakerFactory)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `reloadPublicKey handles feign exceptions gracefully without circuit breaker`() {
        val request = Request.create(Request.HttpMethod.GET, "/publicKey", emptyMap(), null, RequestTemplate())
        `when`(authenticationClient.publicKey()).thenThrow(FeignException.Unauthorized("Unauthorized", request, null, emptyMap()))

        val validator = JwsValidator(authenticationClient)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `reloadPublicKey handles feign service unavailable gracefully without circuit breaker`() {
        val request = Request.create(Request.HttpMethod.GET, "/publicKey", emptyMap(), null, RequestTemplate())
        `when`(authenticationClient.publicKey()).thenThrow(FeignException.ServiceUnavailable("Unavailable", request, null, emptyMap()))

        val validator = JwsValidator(authenticationClient)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `reloadPublicKey handles generic exception gracefully without circuit breaker`() {
        `when`(authenticationClient.publicKey()).thenThrow(RuntimeException("Network failure"))

        val validator = JwsValidator(authenticationClient)
        assertDoesNotThrow { validator.reloadPublicKey() }
    }

    @Test
    fun `updatePublicKey triggers scheduled method without throwing`() {
        `when`(authenticationClient.publicKey()).thenReturn(validPublicKeyDto)

        val validator = JwsValidator(authenticationClient)
        assertDoesNotThrow { validator.updatePublicKey() }
    }

    @Test
    fun `updatePublicKey skips reloads after a successful reload`() {
        `when`(authenticationClient.publicKey()).thenReturn(validPublicKeyDto)
        val currentTimeNanos = AtomicLong(0)
        val validator = JwsValidator(authenticationClient, nanoTime = currentTimeNanos::get)

        validator.updatePublicKey()
        validator.updatePublicKey()
        currentTimeNanos.addAndGet(TimeUnit.MINUTES.toNanos(10))
        validator.updatePublicKey()

        verify(authenticationClient, times(2)).publicKey()
    }

    @Test
    fun `updatePublicKey keeps retrying when reload fails`() {
        `when`(authenticationClient.publicKey()).thenThrow(RuntimeException("Network failure"))
        val validator = JwsValidator(authenticationClient)

        validator.updatePublicKey()
        validator.updatePublicKey()

        verify(authenticationClient, times(2)).publicKey()
    }
}
