package com.michibaum.gatewayservice.config

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.servlet.function.ServerRequest
import java.net.URI

class ServerRequestExtensionsUT {

    @Test
    fun `authenticate returns unauthorized when user is not authenticated`() {
        val authFilter = mock(ServletAuthenticationFilter::class.java)
        val servletRequest = mock(HttpServletRequest::class.java)
        val serverRequest = mock(ServerRequest::class.java)
        `when`(serverRequest.servletRequest()).thenReturn(servletRequest)
        `when`(authFilter.getAuthentication(servletRequest)).thenReturn(null)

        val response = serverRequest.authenticate(
            URI.create("http://localhost:8080"),
            authFilter,
            Permissions.ADMIN_SERVICE
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode())
    }

    @Test
    fun `authenticate returns unauthorized when user lacks required permission`() {
        val authFilter = mock(ServletAuthenticationFilter::class.java)
        val servletRequest = mock(HttpServletRequest::class.java)
        val serverRequest = mock(ServerRequest::class.java)
        `when`(serverRequest.servletRequest()).thenReturn(servletRequest)

        val auth = TestingAuthenticationToken("user", "pass", listOf(SimpleGrantedAuthority(Permissions.CHESS_SERVICE.name)))
        `when`(authFilter.getAuthentication(servletRequest)).thenReturn(auth)

        val response = serverRequest.authenticate(
            URI.create("http://localhost:8080"),
            authFilter,
            Permissions.ADMIN_SERVICE
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode())
    }

    @Test
    fun `authenticatedHttp handler returns unauthorized when user is not authenticated`() {
        val authFilter = mock(ServletAuthenticationFilter::class.java)
        val servletRequest = mock(HttpServletRequest::class.java)
        val serverRequest = mock(ServerRequest::class.java)
        `when`(serverRequest.servletRequest()).thenReturn(servletRequest)
        `when`(authFilter.getAuthentication(servletRequest)).thenReturn(null)

        val handler = authenticatedHttp(
            URI.create("http://localhost:8080"),
            authFilter,
            Permissions.ADMIN_SERVICE
        )
        val response = handler.handle(serverRequest)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode())
    }
}
