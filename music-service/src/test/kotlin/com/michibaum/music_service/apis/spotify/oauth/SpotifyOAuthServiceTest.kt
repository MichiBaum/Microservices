package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.config.properties.SpotifyOAuthProperties
import com.michibaum.music_service.database.spotify.SpotifyAccountActivatedRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentialsRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthData
import com.michibaum.music_service.database.spotify.SpotifyOAuthDataRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.*

class SpotifyOAuthServiceTest {

    private val spotifyOAuthDataRepository: SpotifyOAuthDataRepository = mockk()
    private val spotifyOAuthCredentialsRepository: SpotifyOAuthCredentialsRepository = mockk()
    private val spotifyAccountActivatedRepository: SpotifyAccountActivatedRepository = mockk()
    private val spotifyOAuthProperties: SpotifyOAuthProperties = mockk()
    private val apisProperties: ApisProperties = mockk()
    private val restClientBuilder: RestClient.Builder = mockk()
    private val restClient: RestClient = mockk()

    private lateinit var service: SpotifyOAuthService

    @BeforeEach
    fun setUp() {
        every { spotifyOAuthProperties.clientId } returns "client_id"
        every { spotifyOAuthProperties.clientSecret } returns "client_secret"
        every { apisProperties.userAgent } returns "test-agent"
        every { restClientBuilder.baseUrl(any<String>()) } returns restClientBuilder
        every { restClientBuilder.defaultHeaders(any()) } returns restClientBuilder
        every { restClientBuilder.build() } returns restClient

        service = SpotifyOAuthService(
            spotifyOAuthDataRepository,
            spotifyOAuthCredentialsRepository,
            spotifyAccountActivatedRepository,
            spotifyOAuthProperties,
            apisProperties,
            restClientBuilder
        )
    }

    @Test
    fun `isSpotifyAccountEnabled returns true when user is activated`() {
        val userId = "user-123"
        every { spotifyAccountActivatedRepository.existsByUserId(userId) } returns true

        val enabled = service.isSpotifyAccountEnabled(userId)

        assertTrue(enabled)
        verify(exactly = 1) { spotifyAccountActivatedRepository.existsByUserId(userId) }
    }

    @Test
    fun `isSpotifyAccountEnabled returns false when user is not activated`() {
        val userId = "user-456"
        every { spotifyAccountActivatedRepository.existsByUserId(userId) } returns false

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

        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse("user-789") } returns emptyList()
        every { spotifyOAuthCredentialsRepository.findByUserIdAndRefreshToken("user-789", "refresh_token_123") } returns null

        val credsSlot = slot<SpotifyOAuthCredentials>()
        every { spotifyOAuthCredentialsRepository.save(capture(credsSlot)) } answers { credsSlot.captured }

        val result = service.save(dto, oAuthData)

        assertNotNull(result)
        assertEquals(longScope, result.scope)
        assertEquals("user-789", result.userId)
        verify { spotifyOAuthCredentialsRepository.save(any()) }
    }

    @Test
    fun `activeCredentialsByUser returns single active credentials`() {
        val userId = "user-123"
        val creds = SpotifyOAuthCredentials(
            accessToken = "access",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )
        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId) } returns listOf(creds)

        val result = service.activeCredentialsByUser(userId)

        assertEquals(creds, result)
        assertFalse(creds.deactivated)
    }

    @Test
    fun `activeCredentialsByUser self-heals when multiple active credentials exist`() {
        val userId = "user-123"
        val olderId = UUID.randomUUID()
        val newerId = UUID.randomUUID()

        val olderCreds = SpotifyOAuthCredentials(
            accessToken = "access1",
            expiresIn = 3600,
            refreshToken = "refresh1",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now().minusSeconds(100),
            validUntil = Instant.now().plusSeconds(3500),
            id = olderId
        )
        val newerCreds = SpotifyOAuthCredentials(
            accessToken = "access2",
            expiresIn = 3600,
            refreshToken = "refresh2",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = newerId
        )

        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId) } returns listOf(olderCreds, newerCreds)
        every { spotifyOAuthCredentialsRepository.save(any()) } answers { firstArg() }

        val result = service.activeCredentialsByUser(userId)

        assertEquals(newerCreds, result)
        assertTrue(olderCreds.deactivated)
        assertFalse(newerCreds.deactivated)
        verify(exactly = 1) { spotifyOAuthCredentialsRepository.save(olderCreds) }
    }

    @Test
    fun `saveCredentials scopes lookup by userId and refreshToken`() {
        val userId = "user-789"
        val dto = SpotifyOAuthCredentialsDto(
            accessToken = "new_access",
            tokenType = "Bearer",
            scope = "user-read-private",
            expiresIn = 3600,
            refreshToken = "shared_refresh_token"
        )

        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId) } returns emptyList()
        every { spotifyOAuthCredentialsRepository.findByUserIdAndRefreshToken(userId, "shared_refresh_token") } returns null

        val credsSlot = slot<SpotifyOAuthCredentials>()
        every { spotifyOAuthCredentialsRepository.save(capture(credsSlot)) } answers { credsSlot.captured }

        val result = service.saveCredentials(dto, userId)

        assertEquals("new_access", result.accessToken)
        assertEquals(userId, result.userId)
        verify(exactly = 1) { spotifyOAuthCredentialsRepository.findByUserIdAndRefreshToken(userId, "shared_refresh_token") }
        verify(exactly = 0) { spotifyOAuthCredentialsRepository.findByRefreshToken(any()) }
    }

    @Test
    fun `saveCredentials falls back to previous refresh token when response refresh token is blank`() {
        val userId = "user-789"
        val dto = SpotifyOAuthCredentialsDto(
            accessToken = "new_access",
            tokenType = "Bearer",
            scope = "user-read-private",
            expiresIn = 3600,
            refreshToken = ""
        )

        val previousCreds = SpotifyOAuthCredentials(
            accessToken = "old_access",
            expiresIn = 3600,
            refreshToken = "fallback_refresh_token",
            scope = "user-read-private",
            userId = userId,
            createdDate = Instant.now().minusSeconds(1000),
            validUntil = Instant.now().minusSeconds(100),
            deactivated = true,
            id = UUID.randomUUID()
        )

        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId) } returns emptyList()
        every { spotifyOAuthCredentialsRepository.findByUserId(userId) } returns listOf(previousCreds)

        val credsSlot = slot<SpotifyOAuthCredentials>()
        every { spotifyOAuthCredentialsRepository.save(capture(credsSlot)) } answers { credsSlot.captured }

        val result = service.saveCredentials(dto, userId)

        assertEquals("new_access", result.accessToken)
        assertEquals("fallback_refresh_token", result.refreshToken)
        assertEquals(userId, result.userId)
    }

    @Test
    fun `refreshToken returns existing credentials if already refreshed by another thread`() {
        val userId = "user-123"
        val validCreds = SpotifyOAuthCredentials(
            accessToken = "refreshed_access",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "scope",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId) } returns listOf(validCreds)

        val result = service.refreshToken(userId)

        assertEquals(validCreds, result)
        verify(exactly = 0) { restClient.post() }
    }
}
