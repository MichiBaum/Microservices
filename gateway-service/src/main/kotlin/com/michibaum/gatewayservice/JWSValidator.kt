package com.michibaum.gatewayservice

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.JWSValidator
import org.springframework.scheduling.annotation.Scheduled
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

class JWSValidator(
    private val authenticationClient: AuthenticationClient
): JWSValidator() {

    private lateinit var publicKey: RSAPublicKey

    fun reloadPublicKey() {
        val dto = authenticationClient.publicKey()
        publicKey = dto.key as RSAPublicKey
    }

    fun validate(token: String): Boolean {
        return validate(token, publicKey)
    }

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    fun updatePublicKey(){
        reloadPublicKey()
    }

}