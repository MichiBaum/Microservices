package com.michibaum.authentication_service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.PublicKeyDto
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant

@Service
class AuthenticationService (
        private val keyPair: KeyPair,
        private val algorithm: Algorithm
) {

    val publicKey: PublicKeyDto
        get() = PublicKeyDto(
                keyPair.public.algorithm,
                keyPair.public.encoded
        )

    fun generateJWS(username: String): String? {
        return JWT.create()
                .withHeader(jwsHeaders())
                .withIssuer("authentication-service")
                .withSubject(username)
                .withExpiresAt(Instant.now().plusSeconds(60*60*8))
                .withIssuedAt(Instant.now())
                .sign(algorithm)
    }

    private fun jwsHeaders(): Map<String, String?> {
        return mapOf(Pair("alg", keyPair.public.algorithm))
    }
}