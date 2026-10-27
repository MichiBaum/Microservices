package com.michibaum.music_service.app.library

import com.michibaum.music_service.apis.spotify.api.SpotifyApi
import com.michibaum.music_service.apis.spotify.generated.model.ArtistObject
import com.michibaum.music_service.apis.spotify.generated.model.CursorPagingSimplifiedArtistObject
import com.michibaum.music_service.apis.spotify.generated.model.FollowersObject
import com.michibaum.music_service.apis.spotify.generated.model.GetFollowed200Response
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.app.artist.ArtistConverter
import com.michibaum.music_service.database.Artist
import com.michibaum.music_service.database.ArtistRepository
import com.michibaum.music_service.database.UserLibraryArtist
import com.michibaum.music_service.database.UserLibraryArtistRepository
import com.michibaum.music_service.database.spotify.ArtistSpotify
import com.michibaum.music_service.database.spotify.ArtistSpotifyRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class MusicLibraryServiceTest {

    private val userLibraryArtistRepository: UserLibraryArtistRepository = mockk()
    private val artistRepository: ArtistRepository = mockk()
    private val artistSpotifyRepository: ArtistSpotifyRepository = mockk()
    private val spotifyOAuthService: SpotifyOAuthService = mockk()
    private val spotifyApi: SpotifyApi = mockk()
    private val artistConverter: ArtistConverter = ArtistConverter()

    private val service = MusicLibraryService(
        userLibraryArtistRepository,
        artistRepository,
        artistSpotifyRepository,
        spotifyOAuthService,
        spotifyApi,
        artistConverter
    )

    @Test
    fun `getLibraryArtists returns list of ArtistDto`() {
        val userId = "user-1"
        val artist = Artist(name = "Radiohead", isni = "0000000121773094", id = UUID.randomUUID())
        val userLibraryArtist = UserLibraryArtist(userId = userId, artist = artist, id = UUID.randomUUID())

        every { userLibraryArtistRepository.findByUserId(userId) } returns listOf(userLibraryArtist)

        val result = service.getLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals(artist.id.toString(), result[0].id)
        assertEquals("Radiohead", result[0].name)
        assertEquals("0000000121773094", result[0].isni)
    }

    @Test
    fun `syncLibraryArtists returns existing library if no spotify credentials`() {
        val userId = "user-1"
        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns null
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()

        val result = service.syncLibraryArtists(userId)

        assertTrue(result.isEmpty())
        verify(exactly = 0) { spotifyApi.myFollowedArtists(any()) }
    }

    @Test
    fun `syncLibraryArtists handles null response or null artists from spotify gracefully`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns credentials
        every { spotifyApi.myFollowedArtists(credentials) } returns GetFollowed200Response(artists = null)
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()

        val result = service.syncLibraryArtists(userId)

        assertTrue(result.isEmpty())
        verify(exactly = 0) { artistRepository.save(any()) }
    }

    @Test
    fun `syncLibraryArtists fetches followed artists and persists new records`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles",
            followers = FollowersObject(total = 50000)
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns emptyList()

        val savedArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        every { artistRepository.saveAll(any<List<Artist>>()) } returns listOf(savedArtist)

        val savedArtistSpotify = ArtistSpotify(artist = savedArtist, spotifyId = "sp-1", followers = 50000, id = UUID.randomUUID())
        every { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) } returns listOf(savedArtistSpotify)

        val savedUserLibraryArtist = UserLibraryArtist(userId = userId, artist = savedArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returnsMany listOf(
            emptyList(),
            listOf(savedUserLibraryArtist)
        )
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(savedUserLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles", result[0].name)
        verify(exactly = 1) { artistRepository.saveAll(any<List<Artist>>()) }
        verify(exactly = 1) { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) }
        verify(exactly = 1) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }

    @Test
    fun `syncLibraryArtists with existing artists in database reuses artist and links user library`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.activeCredentialsByUser(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles",
            followers = FollowersObject(total = 50000)
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        val existingArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        val existingArtistSpotify = ArtistSpotify(artist = existingArtist, spotifyId = "sp-1", followers = 50000, id = UUID.randomUUID())
        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns listOf(existingArtistSpotify)

        val userLibraryArtist = UserLibraryArtist(userId = userId, artist = existingArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returnsMany listOf(
            emptyList(),
            listOf(userLibraryArtist)
        )

        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(userLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles", result[0].name)
        verify(exactly = 0) { artistRepository.saveAll(any<List<Artist>>()) }
        verify(exactly = 0) { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) }
        verify(exactly = 1) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }
}
