package com.michibaum.authentication_service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.PublicKeyDto
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant

@Service
@RequiredArgsConstructor
class AuthenticationService (
        private val keyPair: KeyPair
) {

    val publicKey: PublicKeyDto
        get() = PublicKeyDto(
                keyPair.public.algorithm,
                keyPair.public.encoded
        )

    fun generateJWS(username: String): String? {
        val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
        val algorithm = Algorithm.RSA256(publicKey, privateKey)
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