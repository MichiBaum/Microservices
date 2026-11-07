package com.michibaum.music_service.app.playlist

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.*

class PlaylistControllerTest {

    private val playlistService: PlaylistService = mockk()
    private val controller = PlaylistController(playlistService)
    private lateinit var mockMvc: MockMvc

    private val authenticatedUser: JwtAuthentication = mockk()
    private val unauthenticatedUser: JwtAuthentication = mockk()

    private val testUserId = "user-123"

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        every { authenticatedUser.getUserId() } returns testUserId
        every { authenticatedUser.getName() } returns testUserId
        every { unauthenticatedUser.getUserId() } returns ""
        every { unauthenticatedUser.getName() } returns ""
    }

    @Test
    fun `getPlaylists returns 200 with playlists list when authenticated`() {
        val playlistDto = PlaylistDto(
            id = UUID.randomUUID(),
            name = "My Tunes",
            description = "Some description",
            coverImageUrl = "http://image.jpg",
            isSpotify = false,
            spotifyId = null,
            trackCount = 3,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        every { playlistService.getUserPlaylists(testUserId) } returns listOf(playlistDto)

        mockMvc.perform(get("/api/playlists").principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("My Tunes"))
            .andExpect(jsonPath("$[0].trackCount").value(3))

        mockMvc.perform(get("/api/music/library/playlists").principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("My Tunes"))

        verify(exactly = 2) { playlistService.getUserPlaylists(testUserId) }
    }

    @Test
    fun `getPlaylists returns 401 when unauthenticated`() {
        mockMvc.perform(get("/api/playlists").principal(unauthenticatedUser))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `createPlaylist returns 201 with created playlist`() {
        val generatedId = UUID.randomUUID()
        val createdDto = PlaylistDto(
            id = generatedId,
            name = "New Playlist",
            description = "Desc",
            coverImageUrl = null,
            isSpotify = false,
            spotifyId = null,
            trackCount = 0,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        every {
            playlistService.createPlaylist("New Playlist", "Desc", null, testUserId)
        } returns createdDto

        mockMvc.perform(
            post("/api/playlists")
                .principal(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"New Playlist","description":"Desc"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(generatedId.toString()))
            .andExpect(jsonPath("$.name").value("New Playlist"))
    }

    @Test
    fun `getPlaylist returns 200 with details`() {
        val playlistId = UUID.randomUUID()
        val detailsDto = PlaylistDetailsDto(
            id = playlistId,
            name = "Rock Classics",
            description = null,
            coverImageUrl = null,
            isSpotify = true,
            spotifyId = "sp-123",
            trackCount = 1,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now(),
            tracks = listOf(
                PlaylistTrackDto(
                    trackId = UUID.randomUUID(),
                    name = "Enter Sandman",
                    artists = emptyList(),
                    position = 0,
                    releaseDate = null
                )
            )
        )
        every { playlistService.getPlaylistDetails(playlistId, testUserId) } returns detailsDto

        mockMvc.perform(get("/api/playlists/{id}", playlistId).principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Rock Classics"))
            .andExpect(jsonPath("$.tracks[0].name").value("Enter Sandman"))
    }

    @Test
    fun `updatePlaylist returns 200 when updating internal playlist`() {
        val playlistId = UUID.randomUUID()
        val updatedDto = PlaylistDto(
            id = playlistId,
            name = "Updated Title",
            description = "Updated Desc",
            coverImageUrl = null,
            isSpotify = false,
            spotifyId = null,
            trackCount = 2,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        every {
            playlistService.updatePlaylist(playlistId, "Updated Title", "Updated Desc", null, testUserId)
        } returns updatedDto

        mockMvc.perform(
            put("/api/playlists/{id}", playlistId)
                .principal(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Updated Title","description":"Updated Desc"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Title"))
    }

    @Test
    fun `updatePlaylist returns 400 when Spotify playlist mutation is rejected`() {
        val playlistId = UUID.randomUUID()
        every {
            playlistService.updatePlaylist(playlistId, any(), any(), any(), testUserId)
        } throws ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify playlists cannot be modified manually")

        mockMvc.perform(
            put("/api/playlists/{id}", playlistId)
                .principal(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Changed Name"}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deletePlaylist returns 204`() {
        val playlistId = UUID.randomUUID()
        every { playlistService.deletePlaylist(playlistId, testUserId) } just Runs

        mockMvc.perform(delete("/api/playlists/{id}", playlistId).principal(authenticatedUser))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { playlistService.deletePlaylist(playlistId, testUserId) }
    }

    @Test
    fun `addTrack returns 201 with track details`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val ptDto = PlaylistTrackDto(
            trackId = trackId,
            name = "Nothing Else Matters",
            artists = emptyList(),
            position = 1,
            releaseDate = null
        )
        every {
            playlistService.addTrackToPlaylist(playlistId, trackId, 1, testUserId)
        } returns ptDto

        mockMvc.perform(
            post("/api/playlists/{id}/tracks", playlistId)
                .principal(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"trackId":"$trackId","position":1}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.trackId").value(trackId.toString()))
            .andExpect(jsonPath("$.position").value(1))
    }

    @Test
    fun `removeTrack returns 204`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        every { playlistService.removeTrackFromPlaylist(playlistId, trackId, testUserId) } just Runs

        mockMvc.perform(delete("/api/playlists/{id}/tracks/{trackId}", playlistId, trackId).principal(authenticatedUser))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { playlistService.removeTrackFromPlaylist(playlistId, trackId, testUserId) }
    }

    @Test
    fun `syncPlaylists returns 200 with synced playlists`() {
        val dto = PlaylistDto(
            id = UUID.randomUUID(),
            name = "Synced Spotify List",
            description = null,
            coverImageUrl = null,
            isSpotify = true,
            spotifyId = "sp-1",
            trackCount = 10,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        every { playlistService.syncPlaylists(testUserId, listOf("sp-1")) } returns listOf(dto)

        mockMvc.perform(
            post("/api/playlists/sync")
                .principal(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"spotifyPlaylistIds":["sp-1"]}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Synced Spotify List"))
            .andExpect(jsonPath("$[0].isSpotify").value(true))
    }

    @Test
    fun `syncPlaylistById returns 200 with playlist details`() {
        val playlistId = UUID.randomUUID()
        val detailsDto = PlaylistDetailsDto(
            id = playlistId,
            name = "Single Synced",
            description = null,
            coverImageUrl = null,
            isSpotify = true,
            spotifyId = "sp-9",
            trackCount = 5,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now(),
            tracks = emptyList()
        )
        every { playlistService.syncPlaylistById(playlistId, testUserId) } returns detailsDto

        mockMvc.perform(post("/api/playlists/{id}/sync", playlistId).principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Single Synced"))
            .andExpect(jsonPath("$.isSpotify").value(true))
    }
}
