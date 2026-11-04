package com.michibaum.music_service.apis.spotify.api.user

import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationHandler
import io.micrometer.observation.ObservationRegistry
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
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.*

class SpotifyPlaylistApiImplTest {

    private val spotifyOAuthService: SpotifyOAuthService = mockk()
    private val apisProperties = ApisProperties(userAgent = "TestUserAgent")
    private val restClientBuilder = RestClient.builder()
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var observationRegistry: ObservationRegistry
    private val recordedContexts = mutableListOf<Observation.Context>()
    private lateinit var spotifyPlaylistApi: SpotifyPlaylistApiImpl

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()
        observationRegistry = ObservationRegistry.create()
        recordedContexts.clear()
        observationRegistry.observationConfig().observationHandler(object : ObservationHandler<Observation.Context> {
            override fun onStart(context: Observation.Context) {
                recordedContexts.add(context)
            }
            override fun supportsContext(context: Observation.Context): Boolean = true
        })

        spotifyPlaylistApi = SpotifyPlaylistApiImpl(
            restClientBuilder = restClientBuilder,
            apisProperties = apisProperties,
            spotifyOAuthService = spotifyOAuthService,
            observationRegistry = observationRegistry
        )
    }

    @Test
    fun `myPlaylists succeeds when token is valid`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/playlists"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withSuccess("""{"href":"https://api.spotify.com/v1/me/playlists","limit":20,"offset":0,"total":1,"items":[{"id":"p-1","name":"My Playlist"}]}""", MediaType.APPLICATION_JSON))

        val result = spotifyPlaylistApi.myPlaylists(credentials)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("p-1", result[0].id)
        assertEquals("My Playlist", result[0].name)
        mockServer.verify()
        verify(exactly = 0) { spotifyOAuthService.getActiveToken(any(), any()) }

        val requestObs = recordedContexts.firstOrNull { it.name == "spotify.api.request" }
        assertNotNull(requestObs)
        assertEquals("myPlaylists", requestObs?.getLowCardinalityKeyValue("spotify.method")?.value)
        assertEquals("user-1", requestObs?.getHighCardinalityKeyValue("spotify.user_id")?.value)
        assertEquals("success", requestObs?.getLowCardinalityKeyValue("spotify.status")?.value)
    }

    @Test
    fun `myPlaylists retries with refreshed token on 401 Unauthorized`() {
        val initialCredentials = SpotifyOAuthCredentials(
            accessToken = "expired-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        val refreshedCredentials = SpotifyOAuthCredentials(
            accessToken = "new-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.getActiveToken("user-1", force = true) } returns refreshedCredentials

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/playlists"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
            .andRespond(withUnauthorizedRequest())

        mockServer.expect(requestTo("https://api.spotify.com/v1/me/playlists"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer new-token"))
            .andRespond(withSuccess("""{"href":"https://api.spotify.com/v1/me/playlists","limit":20,"offset":0,"total":1,"items":[{"id":"p-2","name":"Refreshed Playlist"}]}""", MediaType.APPLICATION_JSON))

        val result = spotifyPlaylistApi.myPlaylists(initialCredentials)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("p-2", result[0].id)
        mockServer.verify()
        verify(exactly = 1) { spotifyOAuthService.getActiveToken("user-1", force = true) }

        val requestObs = recordedContexts.firstOrNull { it.name == "spotify.api.request" }
        assertNotNull(requestObs)
        assertEquals("unauthorized", requestObs?.getLowCardinalityKeyValue("spotify.status")?.value)
        assertEquals("success", requestObs?.getLowCardinalityKeyValue("spotify.retry_status")?.value)
    }

    @Test
    fun `getPlaylist succeeds when token is valid`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/playlists/p-123"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withSuccess("""{"id":"p-123","name":"Detailed Playlist","description":"Test"}""", MediaType.APPLICATION_JSON))

        val result = spotifyPlaylistApi.getPlaylist("p-123", credentials)

        assertNotNull(result)
        assertEquals("p-123", result?.id)
        assertEquals("Detailed Playlist", result?.name)
        assertEquals("Test", result?.description)
        mockServer.verify()
    }

    @Test
    fun `getPlaylistTracks succeeds when token is valid`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/playlists/p-123/items"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withSuccess("""{"href":"https://api.spotify.com/v1/playlists/p-123/items","limit":20,"offset":0,"total":1,"items":[{"is_local":false}]}""", MediaType.APPLICATION_JSON))

        val result = spotifyPlaylistApi.getPlaylistTracks("p-123", credentials)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(false, result[0].isLocal)
        mockServer.verify()
    }

    @Test
    fun `getPlaylistTracks throws Unauthorized when refresh fails`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "expired-token",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        every { spotifyOAuthService.getActiveToken("user-1", force = true) } returns null

        mockServer.expect(requestTo("https://api.spotify.com/v1/playlists/p-123/items"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
            .andRespond(withUnauthorizedRequest())

        assertThrows(HttpClientErrorException.Unauthorized::class.java) {
            spotifyPlaylistApi.getPlaylistTracks("p-123", credentials)
        }

        mockServer.verify()
        verify(exactly = 1) { spotifyOAuthService.getActiveToken("user-1", force = true) }

        val requestObs = recordedContexts.firstOrNull { it.name == "spotify.api.request" }
        assertNotNull(requestObs)
        assertEquals("unauthorized", requestObs?.getLowCardinalityKeyValue("spotify.status")?.value)
        assertEquals("refresh_failed", requestObs?.getLowCardinalityKeyValue("spotify.retry_status")?.value)
    }

    @Test
    fun `getPlaylist throws exception on server error`() {
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token-123",
            expiresIn = 3600,
            refreshToken = "refresh-123",
            scope = "playlist-read-private",
            userId = "user-1",
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        mockServer.expect(requestTo("https://api.spotify.com/v1/playlists/p-error"))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-123"))
            .andRespond(withServerError())

        assertThrows(HttpServerErrorException.InternalServerError::class.java) {
            spotifyPlaylistApi.getPlaylist("p-error", credentials)
        }

        mockServer.verify()
        val requestObs = recordedContexts.firstOrNull { it.name == "spotify.api.request" }
        assertNotNull(requestObs)
        assertEquals("error", requestObs?.getLowCardinalityKeyValue("spotify.status")?.value)
    }
}
