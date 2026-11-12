package com.michibaum.authentication_library.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.michibaum.authentication_library.JwsValidationFailed
import com.michibaum.authentication_library.JwsValidationSuccess
import io.micrometer.observation.ObservationRegistry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User

class JwtAuthenticationManagerUT {

    private val jwtService: JwsValidator = mockk()
    private val observationRegistry: ObservationRegistry = ObservationRegistry.create()

    private lateinit var manager: JwtAuthenticationManager

    @BeforeEach
    fun setUp() {
        manager = JwtAuthenticationManager(jwtService, observationRegistry)
    }

    private fun createJwtAuthentication(userId: String? = "test-user-id", username: String = "testuser"): JwtAuthentication {
        val userDetails = User.builder()
            .username(username)
            .password("")
            .authorities(emptyList())
            .build()
        val builder = JWT.create().withSubject(username)
        if (userId != null) {
            builder.withClaim("userId", userId)
        }
        val token = builder.sign(Algorithm.none())
        return JwtAuthentication(token, userDetails)
    }

    @Test
    fun `supports returns true only for JwtAuthentication`() {
        assertTrue(manager.supports(JwtAuthentication::class.java))
        assertFalse(manager.supports(UsernamePasswordAuthenticationToken::class.java))
    }

    @Test
    fun `authenticate returns null for non-JwtAuthentication`() {
        val nonJwtAuth = UsernamePasswordAuthenticationToken("user", "pass")
        val result = manager.authenticate(nonJwtAuth)
        assertNull(result)
    }

    @Test
    fun `authenticate throws InvalidJwtException when token validation fails`() {
        val jwtAuth = createJwtAuthentication()
        every { jwtService.validate(jwtAuth.token) } returns JwsValidationFailed(RuntimeException("Invalid signature"))

        assertThrows(InvalidJwtException::class.java) {
            manager.authenticate(jwtAuth)
        }

        assertFalse(jwtAuth.isAuthenticated)
    }

    @Test
    fun `authenticate throws InvalidJwtException when signed token is missing userId claim`() {
        val jwtAuth = createJwtAuthentication(userId = null)
        val decodedJwt = mockk<DecodedJWT>()
        every { jwtService.validate(jwtAuth.token) } returns JwsValidationSuccess(decodedJwt)

        assertThrows(InvalidJwtException::class.java) {
            manager.authenticate(jwtAuth)
        }

        assertFalse(jwtAuth.isAuthenticated)
    }

    @Test
    fun `authenticate throws InvalidJwtException when signed token has blank userId claim`() {
        val jwtAuth = createJwtAuthentication(userId = "   ")
        val decodedJwt = mockk<DecodedJWT>()
        every { jwtService.validate(jwtAuth.token) } returns JwsValidationSuccess(decodedJwt)

        assertThrows(InvalidJwtException::class.java) {
            manager.authenticate(jwtAuth)
        }

        assertFalse(jwtAuth.isAuthenticated)
    }

    @Test
    fun `authenticate marks authentication as authenticated when validation succeeds and userId is present`() {
        val jwtAuth = createJwtAuthentication(userId = "user-1234")
        val decodedJwt = mockk<DecodedJWT>()
        every { jwtService.validate(jwtAuth.token) } returns JwsValidationSuccess(decodedJwt)

        val result = manager.authenticate(jwtAuth)

        assertEquals(jwtAuth, result)
        assertTrue(jwtAuth.isAuthenticated)
        assertEquals("user-1234", jwtAuth.getUserId())
    }
}
