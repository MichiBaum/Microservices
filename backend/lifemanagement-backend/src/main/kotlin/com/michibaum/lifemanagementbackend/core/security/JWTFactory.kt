package com.michibaum.lifemanagementbackend.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTFactory(
    private val applicationVersion: String,
    private val startingSecret: String
) {

    // CREATE JWT

    fun create(username: String): JWTCreator.Builder? {
        return JWT.create()
            .withSubject(
                username
            )
            .withClaim("backend_version", applicationVersion)
            .withClaim("starting_secret", startingSecret)
            .withExpiresAt(
                Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)
            )
    }

    companion object{
        enum class ALGORITHMS(val spec: String, val param: (secret: String) -> Algorithm) {
            HMAC512("HS512", Algorithm::HMAC512)
        }
    }

    // END CREATE JWT

}

fun JWTCreator.Builder.sign(algorith: JWTFactory.Companion.ALGORITHMS): String? {
    this.withHeader(
        mapOf(
            "alg" to algorith.spec,
            "typ" to "JWT"
        )
    )
    return this.sign(algorith.param(SecurityConstants.SECRET))
}
