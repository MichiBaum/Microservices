package com.michibaum.gatewayservice

import com.michibaum.authentication_library.JWSValidator
import org.springframework.stereotype.Component
import java.security.interfaces.RSAPublicKey

@Component
class AuthenticationValidator: JWSValidator() {

    var publicKey: RSAPublicKey? = null

    fun valid(token: String): Boolean {
        return this.validate(token, publicKey)
    }

}