package com.michibaum.authentication_service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_service.TestcontainersConfiguration
import com.michibaum.usermanagement_library.UserDetailsDto
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@SpringBootTest
@TestcontainersConfiguration
class AuthenticationServiceIT {

    @Autowired
    lateinit var authService: AuthenticationService
    @Autowired
    lateinit var keyPair: KeyPair

    @Test
    fun `jws valid`() {
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "ubdf-sdfg-sdfv-sdfv",
            username = "UName",
            permissions = emptySet()
        )

        // WHEN
        val jws = authService.generateJWS(userDetailsDto)

        // THEN
        assertNotNull(jws)
        val alg = Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
        val verifier = JWT.require(alg).build()
        assertDoesNotThrow { verifier.verify(jws) }
    }

}