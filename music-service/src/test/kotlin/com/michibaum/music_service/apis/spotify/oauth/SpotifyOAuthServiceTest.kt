package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.database.spotify.SpotifyAccountActivatedRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentialsRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthData
import com.michibaum.music_service.database.spotify.SpotifyOAuthDataRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SpotifyOAuthServiceTest {

    private val spotifyOAuthDataRepository: SpotifyOAuthDataRepository = mockk()
    private val spotifyOAuthCredentialsRepository: SpotifyOAuthCredentialsRepository = mockk()
    private val spotifyAccountActivatedRepository: SpotifyAccountActivatedRepository = mockk()

    private val service = SpotifyOAuthService(
        spotifyOAuthDataRepository,
        spotifyOAuthCredentialsRepository,
        spotifyAccountActivatedRepository
    )

    @Test
    fun `isSpotifyAccountEnabled returns true when user is activated`() {
        val userId = "user-123"
        every { spotifyAccountActivatedRepository.existsByUserId(userId) } returns listOf(mockk())

        val enabled = service.isSpotifyAccountEnabled(userId)

        assertTrue(enabled)
        verify(exactly = 1) { spotifyAccountActivatedRepository.existsByUserId(userId) }
    }

    @Test
    fun `isSpotifyAccountEnabled returns false when user is not activated`() {
        val userId = "user-456"
        every { spotifyAccountActivatedRepository.existsByUserId(userId) } returns emptyList()

        val enabled = service.isSpotifyAccountEnabled(userId)

        assertFalse(enabled)
        verify(exactly = 1) { spotifyAccountActivatedRepository.existsByUserId(userId) }
    }

    @Test
    fun `generateData creates state and persists OAuth data`() {
        val principal: JwtAuthentication = mockk()
        every { principal.getUserId() } returns "user-789"

        val dataSlot = slot<SpotifyOAuthData>()
        every { spotifyOAuthDataRepository.save(capture(dataSlot)) } answers { dataSlot.captured }

        val result = service.generateData(principal)

        assertNotNull(result)
        assertEquals("user-789", result.userId)
        assertNotNull(result.state)
        assertTrue(result.state.isNotBlank())
        verify(exactly = 1) { spotifyOAuthDataRepository.save(any()) }
    }

    @Test
    fun `save creates credentials with long scope`() {
        val longScope = "user-read-email user-read-private " + "a".repeat(500)
        val dto = SpotifyOAuthCredentialsDto(
            accessToken = "access_token_123",
            tokenType = "Bearer",
            scope = longScope,
            expiresIn = 3600,
            refreshToken = "refresh_token_123"
        )
        val oAuthData = SpotifyOAuthData(state = "state_123", userId = "user-789")

        val credsSlot = slot<SpotifyOAuthCredentials>()
        every { spotifyOAuthCredentialsRepository.save(capture(credsSlot)) } answers { credsSlot.captured }

        val result = service.save(dto, oAuthData)

        assertNotNull(result)
        assertEquals(longScope, result.scope)
        assertEquals("user-789", result.userId)
        verify(exactly = 1) { spotifyOAuthCredentialsRepository.save(any()) }
    }
}
