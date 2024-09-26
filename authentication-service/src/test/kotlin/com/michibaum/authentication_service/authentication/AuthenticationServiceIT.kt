package com.michibaum.authentication_service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.security.KeyPair
import java.security.interfaces.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@SpringBootTest
class AuthenticationServiceIT {

    @Autowired
    lateinit var authService: AuthenticationService
    @Autowired
    lateinit var keyPair: KeyPair

    @Test
    fun `jwt valid`() {
        // GIVEN

        // WHEN
        var jwt = authService.generateJWS("Michi")

        // THEN
        assertNotNull(jwt)
        var alg = Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
        var verifier = JWT.require(alg).build()
        assertDoesNotThrow { verifier.verify(jwt) }
    }

}