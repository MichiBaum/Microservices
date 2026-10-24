package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.JwsValidationResult
import com.michibaum.authentication_library.JwsValidator
import feign.FeignException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.micrometer.observation.annotation.Observed
import org.slf4j.Logger
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


open class JwsValidator(
    private val authenticationClient: AuthenticationClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>? = null,
    private val nanoTime: () -> Long = { System.nanoTime() }
): JwsValidator() {

    companion object {
        val logger: Logger = org.slf4j.LoggerFactory.getLogger(JwsValidator::class.java)
        private const val SUCCESSFUL_RELOAD_INTERVAL_MINUTES = 10L
        private const val SCHEDULED_RELOAD_INTERVAL_SECONDS = 15L
    }

    private val circuitBreaker by lazy { circuitBreakerFactory?.create("authentication-service-public-key") }
    private var publicKey: RSAPublicKey? = null
    @Volatile private var lastSuccessfulReloadNanos: Long? = null

    open fun reloadPublicKey() {
        val dto = try {
            logger.info("JwsValidator reloading public key")
            val configuredCircuitBreaker = circuitBreaker
            if (configuredCircuitBreaker != null) {
                configuredCircuitBreaker.run({ authenticationClient.publicKey() }, { throwable ->
                    when (throwable) {
                        is CallNotPermittedException -> logger.error("Circuit breaker 'authentication-service-public-key' is OPEN: ${throwable.message}")
                        is TimeoutException -> logger.error("Public key reload timed out: ${throwable.message}")
                        is FeignException.Unauthorized -> logger.error("JwsValidator could not reload public key: ${throwable.message}", throwable)
                        is FeignException.ServiceUnavailable -> logger.error("JwsValidator could not reach authentication server: ${throwable.message}")
                        else -> logger.error("JwsValidator unexpected error during public key reload: ${throwable?.message}", throwable)
                    }
                    null
                }) ?: return
            } else {
                authenticationClient.publicKey()
            }
        } catch (ex: FeignException.Unauthorized) {
            logger.error("JwsValidator could not reload public key: ${ex.message}", ex)
            return
        } catch (ex: FeignException.ServiceUnavailable) {
            logger.error("JwsValidator could not reach authentication server: ${ex.message}")
            return
        } catch (ex: Exception) {
            logger.error("JwsValidator unexpected error during public key reload: ${ex.message}", ex)
            return
        }
        val pubkey: PublicKey = KeyFactory.getInstance(dto.algorithm).generatePublic(X509EncodedKeySpec(dto.key))
        publicKey = pubkey as RSAPublicKey
        lastSuccessfulReloadNanos = nanoTime()
    }

    open fun validate(token: String): JwsValidationResult {
        return validate(token, publicKey)
    }

    @Observed(name = "jws.validator.update.public.key")
    @Scheduled(fixedRate = SCHEDULED_RELOAD_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    open fun updatePublicKey(){
        val lastSuccessfulReload = lastSuccessfulReloadNanos
        if (lastSuccessfulReload != null && nanoTime() - lastSuccessfulReload < TimeUnit.MINUTES.toNanos(SUCCESSFUL_RELOAD_INTERVAL_MINUTES)) {
            return
        }
        reloadPublicKey()
    }

}