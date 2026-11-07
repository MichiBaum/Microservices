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
    fun `getCredentials delegates to refreshToken`() {
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

        every { spotifyOAuthService.getActiveToken(userId) } returns validCredentials

        val result = spotifyOAuthImpl.getCredentials(principal)

        assertEquals(validCredentials, result)
        verify(exactly = 1) { spotifyOAuthService.getActiveToken(userId) }
    }
}
