package com.michibaum.authentication_library

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import java.security.interfaces.RSAPublicKey

open class JwsValidator {
    fun validate(token: String, publicKey: RSAPublicKey): Boolean {
        try {
            val algorithm = JwsAlgorithm.algorithm(publicKey)
            val verifier: JWTVerifier = JWT.require(algorithm)
                    .withIssuer("authentication-service")
                    // TODO check valid until
                    .build() //Reusable verifier instance
            verifier.verify(token)
            return true
        } catch (_: JWTVerificationException){}
        return false
    }

}