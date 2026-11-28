package com.michibaum.authentication_service.app.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.permission_library.Permissions
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

    fun generateJWS(userDetails: UserDetailsDto, username: String): String {
        return JWT.create()
                .withHeader(jwsHeaders())
                .withIssuer("authentication-service")
                .withSubject(username)
                .withExpiresAt(Instant.now().plusSeconds(60*60*8))
                .withIssuedAt(Instant.now())
                .withClaim("userId", userDetails.id)
                .withArrayClaim("permissions", userDetails.permissions.toTypedArray())
                .sign(algorithm)
    }

    fun generateInternalServiceJWS(): String {
        return JWT.create()
                .withHeader(jwsHeaders())
                .withIssuer("authentication-service")
                .withSubject("authentication-service")
                .withExpiresAt(Instant.now().plusSeconds(60*60*8))
                .withIssuedAt(Instant.now())
                .withClaim("userId", "authentication-service")
                .withArrayClaim("permissions", arrayOf(Permissions.AUTHENTICATION_SERVICE.name))
                .sign(algorithm)
    }

    private fun jwsHeaders(): Map<String, String?> {
        return mapOf(Pair("alg", keyPair.public.algorithm))
    }

}