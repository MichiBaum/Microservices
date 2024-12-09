package com.michibaum.authentication_library.security.jwt

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.JwsValidationResult
import com.michibaum.authentication_library.JwsValidator
import feign.FeignException
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit


class JwsValidator(
    private val authenticationClient: AuthenticationClient
): JwsValidator() {

    val logger: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private var publicKey: RSAPublicKey? = null

    fun reloadPublicKey() {
        val dto = try {
             authenticationClient.publicKey()
        } catch (ex: FeignException.Unauthorized){
            logger.info(ex.message, ex)
            logger.error("JwsValidator could not reload public key: ${ex.message}")
            return
        }
        val pubkey: PublicKey = KeyFactory.getInstance(dto.algorithm).generatePublic(X509EncodedKeySpec(dto.key))
        publicKey = pubkey as RSAPublicKey
    }

    fun validate(token: String): JwsValidationResult {
        return validate(token, publicKey)
    }

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    fun updatePublicKey(){
        reloadPublicKey()
    }

}