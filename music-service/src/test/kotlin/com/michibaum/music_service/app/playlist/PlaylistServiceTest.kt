package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.apis.spotify.api.user.SpotifyPlaylistApi
import com.michibaum.music_service.apis.spotify.generated.model.*
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.app.artist.ArtistConverter
import com.michibaum.music_service.database.*
import com.michibaum.music_service.database.spotify.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class PlaylistServiceTest {

    private val playlistRepository: PlaylistRepository = mockk()
    private val playlistSpotifyRepository: PlaylistSpotifyRepository = mockk(relaxed = true)
    private val playlistTrackRepository: PlaylistTrackRepository = mockk(relaxed = true)
    private val trackRepository: TrackRepository = mockk(relaxed = true)
    private val trackSpotifyRepository: TrackSpotifyRepository = mockk(relaxed = true)
    private val artistRepository: ArtistRepository = mockk(relaxed = true)
    private val artistSpotifyRepository: ArtistSpotifyRepository = mockk(relaxed = true)
    private val playlistSpotifyApi: SpotifyPlaylistApi = mockk()
    private val spotifyOAuthService: SpotifyOAuthService = mockk()
    private val artistConverter = ArtistConverter()
    private val playlistConverter = PlaylistConverter(artistConverter)

    private val playlistService = PlaylistService(
        playlistRepository = playlistRepository,
        playlistSpotifyRepository = playlistSpotifyRepository,
        playlistTrackRepository = playlistTrackRepository,
        trackRepository = trackRepository,
        trackSpotifyRepository = trackSpotifyRepository,
        artistRepository = artistRepository,
        artistSpotifyRepository = artistSpotifyRepository,
        playlistSpotifyApi = playlistSpotifyApi,
        spotifyOAuthService = spotifyOAuthService,
        playlistConverter = playlistConverter
    )

    private val testUserId = "user-123"

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { playlistTrackRepository.save(any()) } answers { firstArg() }
        every { playlistTrackRepository.saveAll(any<Iterable<PlaylistTrack>>()) } answers { firstArg() }
        every { playlistSpotifyRepository.save(any()) } answers { firstArg() }
        every { trackRepository.save(any()) } answers { firstArg() }
        every { trackSpotifyRepository.save(any()) } answers { firstArg() }
        every { artistRepository.save(any()) } answers { firstArg() }
        every { artistSpotifyRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `getUserPlaylists returns mapped playlist DTOs with spotify info and track counts`() {
        val p1Id = UUID.randomUUID()
        val p2Id = UUID.randomUUID()
        val p1 = Playlist(name = "Internal Playlist", createdBy = testUserId, id = p1Id)
        val p2 = Playlist(name = "Spotify Playlist", createdBy = testUserId, id = p2Id)

        val spotifyLink = PlaylistSpotify(playlist = p2, spotifyId = "sp-2", id = UUID.randomUUID())

        every { playlistRepository.findAllByOrderByCreatedDesc(testUserId) } returns listOf(p1, p2)
        every { playlistSpotifyRepository.findByPlaylistIdIn(listOf(p1Id, p2Id)) } returns listOf(spotifyLink)
        every { playlistTrackRepository.countByPlaylistIds(listOf(p1Id, p2Id)) } returns listOf(
            arrayOf(p1Id, 5L),
            arrayOf(p2Id, 12L)
        )

        val result = playlistService.getUserPlaylists(testUserId)

        assertEquals(2, result.size)
        val dto1 = result.first { it.id == p1Id }
        assertEquals("Internal Playlist", dto1.name)
        assertFalse(dto1.isSpotify)
        assertNull(dto1.spotifyId)
        assertEquals(5, dto1.trackCount)

        val dto2 = result.first { it.id == p2Id }
        assertEquals("Spotify Playlist", dto2.name)
        assertTrue(dto2.isSpotify)
        assertEquals("sp-2", dto2.spotifyId)
        assertEquals(12, dto2.trackCount)
    }

    @Test
    fun `getPlaylistDetails returns details with ordered tracks`() {
        val playlistId = UUID.randomUUID()
        val playlist = Playlist(name = "My Rock", createdBy = testUserId, id = playlistId)
        val track1 = Track(name = "Song A", artists = mutableSetOf(), releaseDate = LocalDate.now(), id = UUID.randomUUID())
        val pt1 = PlaylistTrack(playlist = playlist, track = track1, position = 0, id = UUID.randomUUID())

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.findByPlaylistId(playlistId) } returns null
        every { playlistTrackRepository.findAllByPlaylistIdWithTracks(playlistId) } returns listOf(pt1)

        val details = playlistService.getPlaylistDetails(playlistId, testUserId)

        assertEquals(playlistId, details.id)
        assertEquals("My Rock", details.name)
        assertFalse(details.isSpotify)
        assertEquals(1, details.tracks.size)
        assertEquals("Song A", details.tracks[0].name)
    }

    @Test
    fun `getPlaylistDetails throws 404 when playlist does not exist`() {
        val playlistId = UUID.randomUUID()
        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns null

        val ex = assertThrows<ResponseStatusException> {
            playlistService.getPlaylistDetails(playlistId, testUserId)
        }
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)
    }

    @Test
    fun `createPlaylist saves and returns new internal playlist`() {
        val slot = slot<Playlist>()
        val generatedId = UUID.randomUUID()
        every { playlistRepository.save(capture(slot)) } answers {
            Playlist(
                name = slot.captured.name,
                description = slot.captured.description,
                coverImageUrl = slot.captured.coverImageUrl,
                createdBy = slot.captured.createdBy,
                created = LocalDateTime.now(),
                updated = LocalDateTime.now(),
                id = generatedId
            )
        }

        val result = playlistService.createPlaylist("Gym Tunes", "Workout mix", "http://gym.jpg", testUserId)

        assertEquals("Gym Tunes", result.name)
        assertEquals("Workout mix", result.description)
        assertEquals("http://gym.jpg", result.coverImageUrl)
        assertFalse(result.isSpotify)
        assertEquals(0, result.trackCount)
        verify(exactly = 1) { playlistRepository.save(any()) }
    }

    @Test
    fun `updatePlaylist updates internal playlist successfully`() {
        val playlistId = UUID.randomUUID()
        val playlist = Playlist(name = "Old Name", description = "Old Desc", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns false
        every { playlistRepository.save(any()) } answers { firstArg() }
        every { playlistTrackRepository.countByPlaylistId(playlistId) } returns 3

        val updated = playlistService.updatePlaylist(playlistId, "New Name", "New Desc", "http://new.jpg", testUserId)

        assertEquals("New Name", updated.name)
        assertEquals("New Desc", updated.description)
        assertEquals("http://new.jpg", updated.coverImageUrl)
        assertEquals(3, updated.trackCount)
        verify(exactly = 1) { playlistRepository.save(playlist) }
    }

    @Test
    fun `updatePlaylist rejects modification of Spotify-linked playlist with 400 Bad Request`() {
        val playlistId = UUID.randomUUID()
        val playlist = Playlist(name = "Spotify Mix", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns true

        val ex = assertThrows<ResponseStatusException> {
            playlistService.updatePlaylist(playlistId, "Hacked Name", null, null, testUserId)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
        assertTrue(ex.reason!!.contains("Spotify playlists cannot be modified manually"))
        verify(exactly = 0) { playlistRepository.save(any()) }
    }

    @Test
    fun `deletePlaylist deletes both regular and Spotify playlists including children`() {
        val playlistId = UUID.randomUUID()
        val playlist = Playlist(name = "Delete Me", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistTrackRepository.deleteByPlaylistId(playlistId) } just Runs
        every { playlistSpotifyRepository.deleteByPlaylistId(playlistId) } just Runs
        every { playlistRepository.delete(playlist) } just Runs

        playlistService.deletePlaylist(playlistId, testUserId)

        verifyOrder {
            playlistTrackRepository.deleteByPlaylistId(playlistId)
            playlistSpotifyRepository.deleteByPlaylistId(playlistId)
            playlistRepository.delete(playlist)
        }
    }

    @Test
    fun `addTrackToPlaylist adds track to internal playlist`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val playlist = Playlist(name = "Mix", createdBy = testUserId, id = playlistId)
        val track = Track(name = "Track A", artists = mutableSetOf(), releaseDate = null, id = trackId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns false
        every { trackRepository.findById(trackId) } returns Optional.of(track)
        every { playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId) } returns null
        every { playlistTrackRepository.countByPlaylistId(playlistId) } returns 2

        val ptSlot = slot<PlaylistTrack>()
        every { playlistTrackRepository.save(capture(ptSlot)) } answers {
            PlaylistTrack(
                playlist = ptSlot.captured.playlist,
                track = ptSlot.captured.track,
                position = ptSlot.captured.position,
                id = UUID.randomUUID()
            )
        }

        val result = playlistService.addTrackToPlaylist(playlistId, trackId, null, testUserId)

        assertEquals(trackId, result.trackId)
        assertEquals("Track A", result.name)
        assertEquals(2, result.position)
    }

    @Test
    fun `addTrackToPlaylist rejects adding track to Spotify-linked playlist with 400 Bad Request`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val playlist = Playlist(name = "Spotify Mix", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns true

        val ex = assertThrows<ResponseStatusException> {
            playlistService.addTrackToPlaylist(playlistId, trackId, 0, testUserId)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
        assertTrue(ex.reason!!.contains("Spotify playlists cannot be modified manually"))
        verify(exactly = 0) { playlistTrackRepository.save(any()) }
    }

    @Test
    fun `removeTrackFromPlaylist removes track from internal playlist`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val playlist = Playlist(name = "Mix", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns false
        every { playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId) } just Runs

        playlistService.removeTrackFromPlaylist(playlistId, trackId, testUserId)

        verify(exactly = 1) { playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId) }
    }

    @Test
    fun `removeTrackFromPlaylist rejects removing track from Spotify-linked playlist with 400 Bad Request`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val playlist = Playlist(name = "Spotify Mix", createdBy = testUserId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, testUserId) } returns playlist
        every { playlistSpotifyRepository.existsByPlaylistId(playlistId) } returns true

        val ex = assertThrows<ResponseStatusException> {
            playlistService.removeTrackFromPlaylist(playlistId, trackId, testUserId)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
        assertTrue(ex.reason!!.contains("Spotify playlists cannot be modified manually"))
        verify(exactly = 0) { playlistTrackRepository.deleteByPlaylistIdAndTrackId(any(), any()) }
    }

    @Test
    fun `syncPlaylists ingests missing tracks and artists and mirrors playlist state`() {
        val creds = SpotifyOAuthCredentials(
            accessToken = "token-abc",
            expiresIn = 3600,
            refreshToken = "refresh-abc",
            scope = "playlist-read-private",
            userId = testUserId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600)
        )
        every { spotifyOAuthService.getActiveToken(testUserId) } returns creds

        val spotifyPlaylistObj = mockk<PlaylistObject> {
            every { id } returns "sp-rock"
            every { name } returns "Hard Rock Spotify"
            every { description } returns "Best Rock"
            every { images } returns listOf(mockk { every { url } returns "http://rock.jpg" })
        }
        every { playlistSpotifyApi.getPlaylist("sp-rock", creds) } returns spotifyPlaylistObj

        val artistObj = mockk<SimplifiedArtistObject> {
            every { id } returns "sp-art-1"
            every { name } returns "Metallica"
        }
        val albumObj = mockk<SimplifiedAlbumObject> {
            every { releaseDate } returns "1986-03-03"
        }
        val trackItem = mockk<PlaylistTrackObjectItem> {
            every { id } returns "sp-trk-1"
            every { name } returns "Battery"
            every { artists } returns listOf(artistObj)
            every { album } returns albumObj
            every { externalIds } returns null
        }
        val playlistTrackObj = mockk<PlaylistTrackObject> {
            every { item } returns trackItem
            every { track } returns null
        }
        every { playlistSpotifyApi.getPlaylistTracks("sp-rock", creds) } returns listOf(playlistTrackObj)

        every { playlistSpotifyRepository.findBySpotifyIdAndUserId("sp-rock", testUserId) } returns null
        every { playlistRepository.save(any()) } answers {
            val p = firstArg<Playlist>()
            Playlist(
                name = p.name,
                description = p.description,
                coverImageUrl = p.coverImageUrl,
                createdBy = p.createdBy,
                id = UUID.randomUUID()
            )
        }

        every { trackSpotifyRepository.findBySpotifyId("sp-trk-1") } returns null
        every { artistSpotifyRepository.findBySpotifyId("sp-art-1") } returns null
        every { artistRepository.save(any()) } answers {
            val a = firstArg<Artist>()
            Artist(name = a.name, isni = null, id = UUID.randomUUID())
        }
        every { trackRepository.save(any()) } answers {
            val t = firstArg<Track>()
            Track(name = t.name, artists = t.artists, releaseDate = t.releaseDate, id = UUID.randomUUID())
        }

        every { playlistRepository.findAllByOrderByCreatedDesc(testUserId) } returns emptyList()

        playlistService.syncPlaylists(testUserId, listOf("sp-rock"))

        verify(exactly = 1) { artistRepository.save(match { it.name == "Metallica" }) }
        verify(exactly = 1) { artistSpotifyRepository.save(match { it.spotifyId == "sp-art-1" }) }
        verify(exactly = 1) { trackRepository.save(match { it.name == "Battery" && it.releaseDate == LocalDate.of(1986, 3, 3) }) }
        verify(exactly = 1) { trackSpotifyRepository.save(match { it.spotifyId == "sp-trk-1" }) }
        verify(exactly = 1) { playlistTrackRepository.deleteByPlaylistId(any()) }
        verify(exactly = 1) { playlistTrackRepository.saveAll(any<List<PlaylistTrack>>()) }
    }

    @Test
    fun `syncPlaylists re-sync updates existing playlist in-place for multi-tenancy`() {
        val creds = SpotifyOAuthCredentials(
            accessToken = "token-abc",
            expiresIn = 3600,
            refreshToken = "refresh-abc",
            scope = "playlist-read-private",
            userId = testUserId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600)
        )
        every { spotifyOAuthService.getActiveToken(testUserId) } returns creds

        val existingPlaylistId = UUID.randomUUID()
        val existingPlaylist = Playlist(name = "Old Spotify Name", createdBy = testUserId, id = existingPlaylistId)
        val existingSpotifyLink = PlaylistSpotify(playlist = existingPlaylist, spotifyId = "sp-rock", id = UUID.randomUUID())

        every { playlistSpotifyRepository.findBySpotifyIdAndUserId("sp-rock", testUserId) } returns existingSpotifyLink

        val updatedSpotifyPlaylist = mockk<PlaylistObject> {
            every { id } returns "sp-rock"
            every { name } returns "New Spotify Name"
            every { description } returns "Updated description"
            every { images } returns listOf(mockk { every { url } returns "http://new-cover.jpg" })
        }
        every { playlistSpotifyApi.getPlaylist("sp-rock", creds) } returns updatedSpotifyPlaylist
        every { playlistSpotifyApi.getPlaylistTracks("sp-rock", creds) } returns emptyList()
        every { playlistRepository.save(any()) } answers { firstArg() }
        every { playlistRepository.findAllByOrderByCreatedDesc(testUserId) } returns listOf(existingPlaylist)
        every { playlistSpotifyRepository.findByPlaylistIdIn(any()) } returns listOf(existingSpotifyLink)
        every { playlistTrackRepository.countByPlaylistIds(any()) } returns emptyList()

        val results = playlistService.syncPlaylists(testUserId, listOf("sp-rock"))

        assertEquals("New Spotify Name", existingPlaylist.name)
        assertEquals("Updated description", existingPlaylist.description)
        assertEquals("http://new-cover.jpg", existingPlaylist.coverImageUrl)
        verify(exactly = 1) { playlistTrackRepository.deleteByPlaylistId(existingPlaylistId) }
    }
}
