package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.*

class SpotifyUserApiImplTest {

    private val spotifyOAuthService: SpotifyOAuthService = mockk()
    private val apisProperties = ApisProperties(userAgent = "TestUserAgent")
    private val restClientBuilder = RestClient.builder()
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var spotifyUserApi: SpotifyUserApiImpl

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()
        spotifyUserApi = SpotifyUserApiImpl(
            restClientBuilder = restClientBuilder,
            apisProperties = apisProperties,
            spotifyOAuthService = spotifyOAuthService
        )
    }

    @Test
    fun `myFollowedArtists succeeds when token is valid`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "user-follow-read",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/following?type=artist&limit=50"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withSuccess("""{"artists":{"items":[{"id":"sp-1","name":"Radiohead"}]}}""", MediaType.APPLICATION_JSON))

        val result = spotifyUserApi.myFollowedArtists(credentials)

        assertNotNull(result)
        assertEquals(1, result?.artists?.items?.size)
        assertEquals("Radiohead", result?.artists?.items?.get(0)?.name)
        mockServer.verify()
        verify(exactly = 0) { spotifyOAuthService.getActiveToken(any(), any()) }
    }

    @Test
    fun `myFollowedArtists retries with refreshed token on 401 Unauthorized`() {
        val initialCredentials = SpotifyOAuthCredentials(
            accessToken = "expired-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "user-follow-read",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        val refreshedCredentials = SpotifyOAuthCredentials(
            accessToken = "new-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "user-follow-read",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.getActiveToken("user-1", force = true) } returns refreshedCredentials

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/following?type=artist&limit=50"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
            .andRespond(withUnauthorizedRequest())

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/following?type=artist&limit=50"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer new-token"))
            .andRespond(withSuccess("""{"artists":{"items":[{"id":"sp-1","name":"Radiohead"}]}}""", MediaType.APPLICATION_JSON))

        val result = spotifyUserApi.myFollowedArtists(initialCredentials)

        assertNotNull(result)
        assertEquals("Radiohead", result?.artists?.items?.get(0)?.name)
        mockServer.verify()
        verify(exactly = 1) { spotifyOAuthService.getActiveToken("user-1", force = true) }
    }

    @Test
    fun `myFollowedArtists rethrows 401 when token refresh fails`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "expired-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "user-follow-read",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.getActiveToken("user-1", force = true) } returns null

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/following?type=artist&limit=50"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
            .andRespond(withUnauthorizedRequest())

        assertThrows(HttpClientErrorException.Unauthorized::class.java) {
            spotifyUserApi.myFollowedArtists(credentials)
        }

        mockServer.verify()
        verify(exactly = 1) { spotifyOAuthService.getActiveToken("user-1", force = true) }
    }

    @Test
    fun `myProfile succeeds when token is valid`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "user-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/me"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withSuccess("""{"id":"user-1","display_name":"Test User"}""", MediaType.APPLICATION_JSON))

        val result = spotifyUserApi.myProfile(credentials)

        assertNotNull(result)
        assertEquals("user-1", result?.id)
        mockServer.verify()
    }
}
