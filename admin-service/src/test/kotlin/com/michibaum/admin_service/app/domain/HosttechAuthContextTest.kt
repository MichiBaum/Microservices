package com.michibaum.admin_service.app.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class HosttechAuthContextTest {

    @BeforeEach
    fun setUp() {
        HosttechAuthContext.clear()
    }

    @Test
    fun `setToken and getToken work correctly`() {
        val token = "test-token"
        HosttechAuthContext.setToken(token)
        assertEquals(token, HosttechAuthContext.getToken())
    }

    @Test
    fun `clear removes the token`() {
        HosttechAuthContext.setToken("some-token")
        HosttechAuthContext.clear()
        assertNull(HosttechAuthContext.getToken())
    }

    @Test
    fun `token is isolated between threads`() {
        val token1 = "token-1"
        val token2 = "token-2"
        HosttechAuthContext.setToken(token1)

        val t2 = thread {
            HosttechAuthContext.setToken(token2)
            assertEquals(token2, HosttechAuthContext.getToken())
        }
        t2.join()

        assertEquals(token1, HosttechAuthContext.getToken())
    }

    @Test
    fun `withToken sets and restores the token`() {
        val initialToken = "initial"
        val scopedToken = "scoped"
        HosttechAuthContext.setToken(initialToken)

        val result = HosttechAuthContext.withToken(scopedToken) {
            assertEquals(scopedToken, HosttechAuthContext.getToken())
            "success"
        }

        assertEquals("success", result)
        assertEquals(initialToken, HosttechAuthContext.getToken())
    }

    @Test
    fun `withToken clears token if it was null before`() {
        val scopedToken = "scoped"
        assertNull(HosttechAuthContext.getToken())

        HosttechAuthContext.withToken(scopedToken) {
            assertEquals(scopedToken, HosttechAuthContext.getToken())
        }

        assertNull(HosttechAuthContext.getToken())
    }

    @Test
    fun `withToken restores token even on exception`() {
        val initialToken = "initial"
        val scopedToken = "scoped"
        HosttechAuthContext.setToken(initialToken)

        try {
            HosttechAuthContext.withToken(scopedToken) {
                assertEquals(scopedToken, HosttechAuthContext.getToken())
                throw RuntimeException("test error")
            }
        } catch (e: Exception) {
            // expected
        }

        assertEquals(initialToken, HosttechAuthContext.getToken())
    }
}
