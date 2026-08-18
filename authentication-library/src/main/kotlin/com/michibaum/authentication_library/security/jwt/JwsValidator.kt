package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.JwsValidationResult
import com.michibaum.authentication_library.JwsValidator
import feign.FeignException
import io.micrometer.observation.annotation.Observed
import org.slf4j.Logger
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit


open class JwsValidator(
    private val authenticationClient: AuthenticationClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>? = null
): JwsValidator() {

    companion object {
        val logger: Logger = org.slf4j.LoggerFactory.getLogger(JwsValidator::class.java)
    }

    private var publicKey: RSAPublicKey? = null

    open fun reloadPublicKey() {
        val dto = try {
            logger.info("JwsValidator reloading public key")
            if (circuitBreakerFactory != null) {
                val circuitBreaker = circuitBreakerFactory.create("authentication-service-public-key")
                circuitBreaker.run({ authenticationClient.publicKey() }, { throwable ->
                    logger.error("Circuit breaker for public key reload opened: ${throwable?.message}")
                    throw throwable ?: Exception("Circuit breaker opened with unknown error")
                })
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
    }

    open fun validate(token: String): JwsValidationResult {
        return validate(token, publicKey)
    }

    @Observed(name = "jws.validator.update.public.key")
    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    open fun updatePublicKey(){
        reloadPublicKey()
    }

}