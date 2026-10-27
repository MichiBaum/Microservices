package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SpotifyOAuthImplTest {

    private val spotifyOAuthService: SpotifyOAuthService = mockk()
    private val spotifyOAuthImpl = SpotifyOAuthImpl(spotifyOAuthService)

    @Test
    fun `getCredentials returns active credentials when valid`() {
        val principal: JwtAuthentication = mockk()
        val userId = "user-1"
        every { principal.getUserId() } returns userId

        val validCredentials = SpotifyOAuthCredentials(
            accessToken = "token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns validCredentials

        val result = spotifyOAuthImpl.getCredentials(principal)

        assertEquals(validCredentials, result)
        verify(exactly = 0) { spotifyOAuthService.refreshToken(any()) }
    }

    @Test
    fun `getCredentials triggers refreshToken when credentials are expiring within 5 minutes`() {
        val principal: JwtAuthentication = mockk()
        val userId = "user-1"
        every { principal.getUserId() } returns userId

        val expiringCredentials = SpotifyOAuthCredentials(
            accessToken = "old_token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now().minusSeconds(3500),
            validUntil = Instant.now().plusSeconds(100), // Within 5-minute (300s) buffer
            id = UUID.randomUUID()
        )

        val refreshedCredentials = SpotifyOAuthCredentials(
            accessToken = "new_token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns expiringCredentials
        every { spotifyOAuthService.refreshToken(userId) } returns refreshedCredentials

        val result = spotifyOAuthImpl.getCredentials(principal)

        assertEquals(refreshedCredentials, result)
        verify(exactly = 1) { spotifyOAuthService.refreshToken(userId) }
    }
}
