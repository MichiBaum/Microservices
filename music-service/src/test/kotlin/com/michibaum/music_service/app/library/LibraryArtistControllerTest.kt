package com.michibaum.music_service.app.library

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.app.artist.ArtistDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class LibraryArtistControllerTest {

    private val musicLibraryService = mockk<MusicLibraryService>()
    private val controller = LibraryArtistController(musicLibraryService)
    private lateinit var mockMvc: MockMvc

    private val authenticatedUser: JwtAuthentication = mockk()
    private val unauthenticatedUser: JwtAuthentication = mockk()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        every { authenticatedUser.getUserId() } returns "user-123"
        every { authenticatedUser.getName() } returns "user-123"
        every { unauthenticatedUser.getUserId() } returns ""
        every { unauthenticatedUser.getName() } returns ""
    }

    @Test
    fun `getLibraryArtists returns 200 and list of artists when authenticated`() {
        val artistDto = ArtistDto(id = UUID.randomUUID().toString(), name = "Daft Punk", isni = null)
        every { musicLibraryService.getLibraryArtists("user-123") } returns listOf(artistDto)

        mockMvc.perform(get("/api/music/library/artists").principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Daft Punk"))

        verify(exactly = 1) { musicLibraryService.getLibraryArtists("user-123") }
    }

    @Test
    fun `getLibraryArtists returns 401 when userId is blank`() {
        mockMvc.perform(get("/api/music/library/artists").principal(unauthenticatedUser))
            .andExpect(status().isUnauthorized)

        verify(exactly = 0) { musicLibraryService.getLibraryArtists(any()) }
    }

    @Test
    fun `syncLibraryArtists returns 200 and updated artists when authenticated`() {
        val artistDto = ArtistDto(id = UUID.randomUUID().toString(), name = "Daft Punk", isni = null)
        every { musicLibraryService.syncLibraryArtists("user-123") } returns listOf(artistDto)

        mockMvc.perform(post("/api/music/library/artists/sync").principal(authenticatedUser))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Daft Punk"))

        verify(exactly = 1) { musicLibraryService.syncLibraryArtists("user-123") }
    }

    @Test
    fun `syncLibraryArtists returns 401 when userId is blank`() {
        mockMvc.perform(post("/api/music/library/artists/sync").principal(unauthenticatedUser))
            .andExpect(status().isUnauthorized)

        verify(exactly = 0) { musicLibraryService.syncLibraryArtists(any()) }
    }
}
