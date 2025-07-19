package com.michibaum.authentication_service.app.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.usermanagement_library.UserDetailsDto
import org.springframework.stereotype.Service
import java.security.KeyPair
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

    fun generateJWS(userDetails: UserDetailsDto): String? {
        return JWT.create()
                .withHeader(jwsHeaders())
                .withIssuer("authentication-service")
                .withSubject(userDetails.username)
                .withExpiresAt(Instant.now().plusSeconds(60*60*8))
                .withIssuedAt(Instant.now())
                .withClaim("userId", userDetails.id)
                .withArrayClaim("permissions", userDetails.permissions.toTypedArray())
                .sign(algorithm)
    }

    private fun jwsHeaders(): Map<String, String?> {
        return mapOf(Pair("alg", keyPair.public.algorithm))
    }
}