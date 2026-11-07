package com.michibaum.music_service.app.library

import com.michibaum.music_service.apis.spotify.api.SpotifyApi
import com.michibaum.music_service.apis.spotify.generated.model.ArtistObject
import com.michibaum.music_service.apis.spotify.generated.model.CursorObject
import com.michibaum.music_service.apis.spotify.generated.model.CursorPagingSimplifiedArtistObject
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
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
        every { spotifyOAuthService.getActiveToken(userId) } returns null
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()

        val result = service.syncLibraryArtists(userId)

        assertTrue(result.isEmpty())
        verify(exactly = 0) { spotifyApi.myFollowedArtists(any()) }
    }

    @Test
    fun `syncLibraryArtists handles null response or null artists from spotify gracefully`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials
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
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles"
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns emptyList()

        val savedArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        every { artistRepository.saveAll(any<List<Artist>>()) } returns listOf(savedArtist)

        val savedArtistSpotify = ArtistSpotify(artist = savedArtist, spotifyId = "sp-1", id = UUID.randomUUID())
        every { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) } returns listOf(savedArtistSpotify)

        val savedUserLibraryArtist = UserLibraryArtist(userId = userId, artist = savedArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(savedUserLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles", result[0].name)
        verify(exactly = 1) { artistRepository.saveAll(any<List<Artist>>()) }
        verify(exactly = 1) { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) }
        verify(exactly = 1) { userLibraryArtistRepository.findByUserId(userId) }
        verify(exactly = 1) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }

    @Test
    fun `syncLibraryArtists with existing artists in database reuses artist and links user library`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles"
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        val existingArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        val existingArtistSpotify = ArtistSpotify(artist = existingArtist, spotifyId = "sp-1", id = UUID.randomUUID())
        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns listOf(existingArtistSpotify)

        val userLibraryArtist = UserLibraryArtist(userId = userId, artist = existingArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()

        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(userLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles", result[0].name)
        verify(exactly = 0) { artistRepository.saveAll(any<List<Artist>>()) }
        verify(exactly = 0) { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) }
        verify(exactly = 1) { userLibraryArtistRepository.findByUserId(userId) }
        verify(exactly = 1) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }

    @Test
    fun `syncLibraryArtists updates existing artists when name changes`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles (Remastered)"
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        val existingArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        val existingArtistSpotify = ArtistSpotify(artist = existingArtist, spotifyId = "sp-1", id = UUID.randomUUID())
        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns listOf(existingArtistSpotify)

        val updatedArtistSlot = slot<List<Artist>>()
        every { artistRepository.saveAll(capture(updatedArtistSlot)) } answers { updatedArtistSlot.captured }

        val updatedArtistSpotifySlot = slot<List<ArtistSpotify>>()
        every { artistSpotifyRepository.saveAll(capture(updatedArtistSpotifySlot)) } answers { updatedArtistSpotifySlot.captured }

        val updatedArtist = existingArtist.copy(name = "The Beatles (Remastered)")
        val userLibraryArtist = UserLibraryArtist(userId = userId, artist = updatedArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(userLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles (Remastered)", result[0].name)

        verify(exactly = 1) { artistRepository.saveAll(any<List<Artist>>()) }
        assertEquals(1, updatedArtistSlot.captured.size)
        assertEquals("The Beatles (Remastered)", updatedArtistSlot.captured[0].name)

        verify(exactly = 1) { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) }
        assertEquals(1, updatedArtistSpotifySlot.captured.size)
        assertEquals("The Beatles (Remastered)", updatedArtistSpotifySlot.captured[0].artist.name)

        verify(exactly = 1) { userLibraryArtistRepository.findByUserId(userId) }
        verify(exactly = 1) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }

    @Test
    fun `syncLibraryArtists when artists already in user library does not save duplicate library records`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val spotifyArtist = ArtistObject(
            id = "sp-1",
            name = "The Beatles"
        )
        val getFollowedResponse = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(items = listOf(spotifyArtist))
        )
        every { spotifyApi.myFollowedArtists(credentials) } returns getFollowedResponse

        val existingArtist = Artist(name = "The Beatles", isni = null, id = UUID.randomUUID())
        val existingArtistSpotify = ArtistSpotify(artist = existingArtist, spotifyId = "sp-1", id = UUID.randomUUID())
        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns listOf(existingArtistSpotify)

        val userLibraryArtist = UserLibraryArtist(userId = userId, artist = existingArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns listOf(userLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("The Beatles", result[0].name)
        verify(exactly = 1) { userLibraryArtistRepository.findByUserId(userId) }
        verify(exactly = 0) { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) }
    }

    @Test
    fun `syncLibraryArtists fetches multiple pages of followed artists using cursor pagination`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val page1Artist = ArtistObject(id = "sp-1", name = "Artist Page 1")
        val page2Artist = ArtistObject(id = "sp-2", name = "Artist Page 2")

        val page1Response = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(
                items = listOf(page1Artist),
                next = "https://api.spotify.com/v1/me/following?type=artist&limit=50&after=cursor-1",
                cursors = CursorObject(after = "cursor-1")
            )
        )
        val page2Response = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(
                items = listOf(page2Artist),
                next = null,
                cursors = CursorObject(after = null)
            )
        )

        every { spotifyApi.myFollowedArtists(credentials, null) } returns page1Response
        every { spotifyApi.myFollowedArtists(credentials, "cursor-1") } returns page2Response

        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1", "sp-2")) } returns emptyList()

        val savedArtist1 = Artist(name = "Artist Page 1", isni = null, id = UUID.randomUUID())
        val savedArtist2 = Artist(name = "Artist Page 2", isni = null, id = UUID.randomUUID())
        every { artistRepository.saveAll(any<List<Artist>>()) } returns listOf(savedArtist1, savedArtist2)

        val savedArtistSpotify1 = ArtistSpotify(artist = savedArtist1, spotifyId = "sp-1", id = UUID.randomUUID())
        val savedArtistSpotify2 = ArtistSpotify(artist = savedArtist2, spotifyId = "sp-2", id = UUID.randomUUID())
        every { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) } returns listOf(savedArtistSpotify1, savedArtistSpotify2)

        val savedUserLibraryArtist1 = UserLibraryArtist(userId = userId, artist = savedArtist1, id = UUID.randomUUID())
        val savedUserLibraryArtist2 = UserLibraryArtist(userId = userId, artist = savedArtist2, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(savedUserLibraryArtist1, savedUserLibraryArtist2)

        val result = service.syncLibraryArtists(userId)

        assertEquals(2, result.size)
        verify(exactly = 1) { spotifyApi.myFollowedArtists(credentials, null) }
        verify(exactly = 1) { spotifyApi.myFollowedArtists(credentials, "cursor-1") }
    }

    @Test
    fun `syncLibraryArtists falls back to existing library when general exception occurs during sync`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()

        every { spotifyOAuthService.getActiveToken(userId) } returns credentials
        every { spotifyApi.myFollowedArtists(credentials, null) } throws RuntimeException("Spotify API error")
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()

        val result = service.syncLibraryArtists(userId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `syncLibraryArtists filters out invalid or duplicate artists from spotify payload`() {
        val userId = "user-1"
        val credentials: SpotifyOAuthCredentials = mockk()
        every { spotifyOAuthService.getActiveToken(userId) } returns credentials

        val validArtist1 = ArtistObject(id = "sp-1", name = "Valid Artist")
        val duplicateArtist1 = ArtistObject(id = "sp-1", name = "Valid Artist Duplicate")
        val blankIdArtist = ArtistObject(id = "", name = "Blank ID")
        val nullIdArtist = ArtistObject(id = null, name = "Null ID")
        val blankNameArtist = ArtistObject(id = "sp-2", name = "   ")
        val nullNameArtist = ArtistObject(id = "sp-3", name = null)

        val response = GetFollowed200Response(
            artists = CursorPagingSimplifiedArtistObject(
                items = listOf(validArtist1, duplicateArtist1, blankIdArtist, nullIdArtist, blankNameArtist, nullNameArtist)
            )
        )
        every { spotifyApi.myFollowedArtists(credentials, null) } returns response

        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) } returns emptyList()

        val savedArtist = Artist(name = "Valid Artist", isni = null, id = UUID.randomUUID())
        every { artistRepository.saveAll(any<List<Artist>>()) } returns listOf(savedArtist)

        val savedArtistSpotify = ArtistSpotify(artist = savedArtist, spotifyId = "sp-1", id = UUID.randomUUID())
        every { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) } returns listOf(savedArtistSpotify)

        val savedUserLibraryArtist = UserLibraryArtist(userId = userId, artist = savedArtist, id = UUID.randomUUID())
        every { userLibraryArtistRepository.findByUserId(userId) } returns emptyList()
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(savedUserLibraryArtist)

        val result = service.syncLibraryArtists(userId)

        assertEquals(1, result.size)
        assertEquals("Valid Artist", result[0].name)
        verify(exactly = 1) { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-1")) }
    }

    @Test
    fun `persistSyncedArtists persists new and existing artists correctly`() {
        val userId = "user-1"
        val spotifyArtistNew = ArtistObject(id = "sp-new", name = "New Artist")
        val spotifyArtistExisting = ArtistObject(id = "sp-exist", name = "Existing Artist")

        val existingArtist = Artist(name = "Existing Artist", isni = null, id = UUID.randomUUID())
        val existingArtistSpotify = ArtistSpotify(artist = existingArtist, spotifyId = "sp-exist", id = UUID.randomUUID())

        every { artistSpotifyRepository.findBySpotifyIdIn(listOf("sp-new", "sp-exist")) } returns listOf(existingArtistSpotify)

        val newSavedArtist = Artist(name = "New Artist", isni = null, id = UUID.randomUUID())
        every { artistRepository.saveAll(any<List<Artist>>()) } returns listOf(newSavedArtist)

        val newSavedArtistSpotify = ArtistSpotify(artist = newSavedArtist, spotifyId = "sp-new", id = UUID.randomUUID())
        every { artistSpotifyRepository.saveAll(any<List<ArtistSpotify>>()) } returns listOf(newSavedArtistSpotify)

        val existingUserLib = UserLibraryArtist(userId = userId, artist = existingArtist, id = UUID.randomUUID())
        val newUserLib = UserLibraryArtist(userId = userId, artist = newSavedArtist, id = UUID.randomUUID())

        every { userLibraryArtistRepository.findByUserId(userId) } returns listOf(existingUserLib)
        every { userLibraryArtistRepository.saveAll(any<List<UserLibraryArtist>>()) } returns listOf(newUserLib)

        val result = service.persistSyncedArtists(userId, listOf(spotifyArtistNew, spotifyArtistExisting))

        assertEquals(2, result.size)
        val names = result.map { it.name }
        assertTrue(names.contains("Existing Artist"))
        assertTrue(names.contains("New Artist"))
    }
}
