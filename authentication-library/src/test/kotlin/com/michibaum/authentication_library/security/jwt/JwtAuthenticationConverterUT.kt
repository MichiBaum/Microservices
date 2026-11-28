package com.michibaum.authentication_library.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.permission_library.Permissions
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

class JwtAuthenticationConverterUT {

    private val converter = JwtAuthenticationConverter()

    private fun createValidToken(userId: String = "test-user-id", username: String = "testuser"): String {
        return JWT.create()
            .withSubject(username)
            .withClaim("userId", userId)
            .withArrayClaim("permissions", arrayOf(Permissions.AUTHENTICATION_SERVICE.name))
            .sign(Algorithm.none())
    }

    @Test
    fun `convert returns null when neither header nor cookie is present`() {
        val request = MockHttpServletRequest()
        val auth = converter.convert(request)
        assertNull(auth)
    }

    @Test
    fun `convert returns JwtAuthentication when valid Bearer header is present`() {
        val token = createValidToken()
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $token")
        }

        val auth = converter.convert(request)
        assertNotNull(auth)
        val jwtAuth = auth as JwtAuthentication
        assertEquals(token, jwtAuth.token)
        assertEquals("testuser", jwtAuth.getUsername())
        assertEquals("test-user-id", jwtAuth.getUserId())
    }

    @Test
    fun `convert returns JwtAuthentication when valid Authentication cookie is present`() {
        val token = createValidToken()
        val request = MockHttpServletRequest().apply {
            setCookies(Cookie("Authentication", token))
        }

        val auth = converter.convert(request)
        assertNotNull(auth)
        val jwtAuth = auth as JwtAuthentication
        assertEquals(token, jwtAuth.token)
        assertEquals("testuser", jwtAuth.getUsername())
    }

    @Test
    fun `convert throws InvalidJwtException when Bearer token is malformed`() {
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer not-a-valid-jwt")
        }

        assertThrows(InvalidJwtException::class.java) {
            converter.convert(request)
        }
    }

    @Test
    fun `convert throws InvalidJwtException when Bearer token is blank`() {
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer ")
        }

        assertThrows(InvalidJwtException::class.java) {
            converter.convert(request)
        }
    }
}
