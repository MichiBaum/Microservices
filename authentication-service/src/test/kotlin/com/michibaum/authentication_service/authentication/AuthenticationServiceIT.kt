package com.michibaum.authentication_service.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_service.TestcontainersConfiguration
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.usermanagement_library.UserDetailsDto
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `jws valid with explicit username and user details`() {
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "ubdf-sdfg-sdfv-sdfv",
            permissions = setOf("READ_USERS", "WRITE_USERS")
        )

        // WHEN
        val jws = authService.generateJWS(userDetailsDto, "UName")

        // THEN
        assertNotNull(jws)
        val alg = Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
        val verifier = JWT.require(alg).build()
        assertDoesNotThrow { verifier.verify(jws) }
        val decoded = JWT.decode(jws)
        assertEquals("UName", decoded.subject)
        assertEquals("ubdf-sdfg-sdfv-sdfv", decoded.getClaim("userId").asString())
        assertEquals(listOf("READ_USERS", "WRITE_USERS"), decoded.getClaim("permissions").asList(String::class.java))
    }

    @Test
    fun `jws valid with empty permissions`() {
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "user-id-456",
            permissions = emptySet()
        )

        // WHEN
        val jws = authService.generateJWS(userDetailsDto, "CustomUsername")

        // THEN
        assertNotNull(jws)
        val decoded = JWT.decode(jws)
        assertEquals("CustomUsername", decoded.subject)
        assertEquals("user-id-456", decoded.getClaim("userId").asString())
        assertEquals(emptyList<String>(), decoded.getClaim("permissions").asList(String::class.java))
    }

}