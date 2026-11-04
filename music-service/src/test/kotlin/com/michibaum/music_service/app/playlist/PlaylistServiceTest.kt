package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.apis.spotify.api.user.SpotifyPlaylistApi
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistObject
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistTrackObject
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistTrackObjectTrack
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.database.*
import com.michibaum.music_service.database.spotify.PlaylistSpotifyRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class PlaylistServiceTest {

    private val playlistRepository: PlaylistRepository = mockk()
    private val playlistSpotifyRepository: PlaylistSpotifyRepository = mockk(relaxed = true)
    private val playlistSyncJobRepository: PlaylistSyncJobRepository = mockk(relaxed = true)
    private val playlistTrackRepository: PlaylistTrackRepository = mockk(relaxed = true)
    private val trackRepository: TrackRepository = mockk()
    private val playlistSpotifyApi: SpotifyPlaylistApi = mockk()
    private val spotifyOAuthService: SpotifyOAuthService = mockk()

    private val playlistService = PlaylistService(
        playlistRepository = playlistRepository,
        playlistSpotifyRepository = playlistSpotifyRepository,
        playlistSyncJobRepository = playlistSyncJobRepository,
        playlistTrackRepository = playlistTrackRepository,
        trackRepository = trackRepository,
        playlistSpotifyApi = playlistSpotifyApi,
        spotifyOAuthService = spotifyOAuthService
    )

    @BeforeEach
    fun setUp() {
        every { playlistSyncJobRepository.save(any()) } answers { firstArg() }
        every { playlistTrackRepository.save(any()) } answers { firstArg() }
        every { playlistSpotifyRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `createPlaylist creates and returns playlist`() {
        val userId = "user-1"
        val playlistSlot = slot<Playlist>()
        every { playlistRepository.save(capture(playlistSlot)) } answers {
            playlistSlot.captured.copy(id = UUID.randomUUID())
        }

        val result = playlistService.createPlaylist("Favorites", "My desc", "http://image.jpg", userId)

        assertNotNull(result)
        assertEquals("Favorites", result.name)
        assertEquals("My desc", result.description)
        assertEquals("http://image.jpg", result.coverImageUrl)
        assertEquals(userId, result.createdBy)
        verify(exactly = 1) { playlistRepository.save(any()) }
    }

    @Test
    fun `getPlaylist returns playlist when found`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "Rock", createdBy = userId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist

        val result = playlistService.getPlaylist(playlistId, userId)

        assertEquals(playlist, result)
    }

    @Test
    fun `getPlaylist throws when not found`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns null

        val exception = assertThrows<RuntimeException> {
            playlistService.getPlaylist(playlistId, userId)
        }
        assertTrue(exception.message!!.contains("Playlist not found"))
    }

    @Test
    fun `updatePlaylist updates fields and saves`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "Old", description = "Old desc", createdBy = userId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist
        every { playlistRepository.save(any()) } answers { firstArg() }

        val result = playlistService.updatePlaylist(playlistId, "New", "New desc", "http://new.jpg", userId)

        assertEquals("New", result.name)
        assertEquals("New desc", result.description)
        assertEquals("http://new.jpg", result.coverImageUrl)
        verify(exactly = 1) { playlistRepository.save(playlist) }
    }

    @Test
    fun `deletePlaylist deletes children and playlist`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "Delete me", createdBy = userId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist
        every { playlistRepository.delete(playlist) } just Runs

        playlistService.deletePlaylist(playlistId, userId)

        verifyOrder {
            playlistTrackRepository.deleteByPlaylistId(playlistId)
            playlistSpotifyRepository.deleteByPlaylistId(playlistId)
            playlistSyncJobRepository.deleteByPlaylistId(playlistId)
            playlistRepository.delete(playlist)
        }
    }

    @Test
    fun `addTrackToPlaylist adds new track`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "List", createdBy = userId, id = playlistId)
        val track = Track(name = "Song", artists = mutableSetOf(), releaseDate = null, id = trackId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist
        every { playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId) } returns null
        every { trackRepository.findById(trackId) } returns Optional.of(track)
        every { playlistTrackRepository.save(any()) } answers { firstArg() }

        val result = playlistService.addTrackToPlaylist(playlistId, trackId, 2, userId)

        assertNotNull(result)
        assertEquals(playlist, result.playlist)
        assertEquals(track, result.track)
        assertEquals(2, result.position)
        verify(exactly = 1) { playlistTrackRepository.save(any()) }
    }

    @Test
    fun `addTrackToPlaylist updates existing track position`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "List", createdBy = userId, id = playlistId)
        val track = Track(name = "Song", artists = mutableSetOf(), releaseDate = null, id = trackId)
        val existing = PlaylistTrack(playlist = playlist, track = track, position = 0)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist
        every { playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId) } returns existing
        every { playlistTrackRepository.save(any()) } answers { firstArg() }

        val result = playlistService.addTrackToPlaylist(playlistId, trackId, 5, userId)

        assertEquals(5, result.position)
        verify(exactly = 1) { playlistTrackRepository.save(existing) }
    }

    @Test
    fun `removeTrackFromPlaylist deletes track`() {
        val playlistId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "List", createdBy = userId, id = playlistId)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist

        playlistService.removeTrackFromPlaylist(playlistId, trackId, userId)

        verify(exactly = 1) { playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId) }
    }

    @Test
    fun `getPlaylistTracks returns sorted tracks`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val playlist = Playlist(name = "List", createdBy = userId, id = playlistId)
        val track1 = Track(name = "Song 1", artists = mutableSetOf(), releaseDate = null, id = UUID.randomUUID())
        val track2 = Track(name = "Song 2", artists = mutableSetOf(), releaseDate = null, id = UUID.randomUUID())

        val pt1 = PlaylistTrack(playlist = playlist, track = track1, position = 3)
        val pt2 = PlaylistTrack(playlist = playlist, track = track2, position = 1)

        every { playlistRepository.findByIdAndCreatedBy(playlistId, userId) } returns playlist
        every { playlistTrackRepository.findByPlaylistId(playlistId) } returns listOf(pt1, pt2)

        val result = playlistService.getPlaylistTracks(playlistId, userId)

        assertEquals(2, result.size)
        assertEquals(1, result[0].position)
        assertEquals(3, result[1].position)
    }

    @Test
    fun `syncPlaylistsFromSpotify throws when empty playlist ids`() {
        assertThrows<IllegalArgumentException> {
            playlistService.syncPlaylistsFromSpotify(emptyList(), "user-1")
        }
    }

    @Test
    fun `syncPlaylistsFromSpotify throws when no credentials`() {
        every { spotifyOAuthService.getActiveToken("user-1") } returns null

        assertThrows<RuntimeException> {
            playlistService.syncPlaylistsFromSpotify(listOf("sp-1"), "user-1")
        }
    }

    @Test
    fun `syncPlaylistsFromSpotify creates playlist and sync job`() {
        val userId = "user-1"
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "read",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )
        val playlistId = UUID.randomUUID()
        val jobId = UUID.randomUUID()

        every { spotifyOAuthService.getActiveToken(userId) } returns credentials
        every { playlistRepository.save(any()) } answers {
            (firstArg() as Playlist).copy(id = playlistId)
        }
        every { playlistSyncJobRepository.save(any()) } answers {
            (firstArg() as PlaylistSyncJob).copy(id = jobId)
        }

        val resultJobId = playlistService.syncPlaylistsFromSpotify(listOf("sp-1"), userId)

        assertEquals(jobId, resultJobId)
        verify(exactly = 1) { playlistRepository.save(any()) }
        verify(exactly = 1) { playlistSyncJobRepository.save(any()) }
    }

    @Test
    fun `processPlaylistSyncs syncs metadata and tracks successfully`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "read",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        val playlist = Playlist(name = "Initial", createdBy = userId, id = playlistId)
        val syncJob = PlaylistSyncJob(playlist = playlist, spotifyPlaylistId = "sp-1", status = SyncJobStatus.PENDING)

        every { playlistSyncJobRepository.findById(playlistId) } returns Optional.of(syncJob)
        every { playlistRepository.findById(playlistId) } returns Optional.of(playlist)
        every { playlistRepository.save(any()) } answers { firstArg() }
        every { playlistSpotifyRepository.findBySpotifyId("sp-1") } returns null
        every { playlistSpotifyRepository.save(any()) } answers { firstArg() }

        val spotifyPlaylist = PlaylistObject(
            id = "sp-1",
            name = "Spotify Name",
            description = "Spotify Desc"
        )
        every { playlistSpotifyApi.getPlaylist("sp-1", credentials) } returns spotifyPlaylist

        val spotifyTrackItem = mockk<PlaylistTrackObjectTrack>()
        every { spotifyTrackItem.id } returns "sp-track-1"
        every { spotifyTrackItem.uri } returns "spotify:track:sptrack1"
        every { spotifyTrackItem.name } returns "Spotify Song"
        every { spotifyTrackItem.artists } returns emptyList()

        val spotifyTrack = PlaylistTrackObject(track = spotifyTrackItem)
        every { playlistSpotifyApi.getPlaylistTracks("sp-1", credentials) } returns listOf(spotifyTrack)

        val dbTrack = Track(name = "Spotify Song", artists = mutableSetOf(), releaseDate = null, id = UUID.randomUUID())
        every { trackRepository.findBySpotifyId("sp-track-1") } returns dbTrack
        every { playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, dbTrack.id!!) } returns null
        every { playlistTrackRepository.save(any()) } answers { firstArg() }

        playlistService.processPlaylistSyncs(playlistId, listOf("sp-1"), credentials)

        assertEquals("Spotify Name", playlist.name)
        assertEquals("Spotify Desc", playlist.description)
        assertEquals(SyncJobStatus.COMPLETED, syncJob.status)
        assertEquals(1, syncJob.trackCount)
        assertNotNull(syncJob.completedAt)
        verify(exactly = 1) { playlistTrackRepository.save(any()) }
    }

    @Test
    fun `processPlaylistSyncs updates job to FAILED on error`() {
        val playlistId = UUID.randomUUID()
        val userId = "user-1"
        val credentials = SpotifyOAuthCredentials(
            accessToken = "token",
            expiresIn = 3600,
            refreshToken = "refresh",
            scope = "read",
            userId = userId,
            createdDate = Instant.now(),
            validUntil = Instant.now().plusSeconds(3600),
            id = UUID.randomUUID()
        )

        val playlist = Playlist(name = "Initial", createdBy = userId, id = playlistId)
        val syncJob = PlaylistSyncJob(playlist = playlist, spotifyPlaylistId = "sp-1", status = SyncJobStatus.PENDING)

        every { playlistSyncJobRepository.findById(playlistId) } returns Optional.of(syncJob)
        every { playlistSpotifyApi.getPlaylist("sp-1", credentials) } throws RuntimeException("Spotify network error")

        playlistService.processPlaylistSyncs(playlistId, listOf("sp-1"), credentials)

        assertEquals(SyncJobStatus.FAILED, syncJob.status)
        assertEquals("Spotify network error", syncJob.errorMessage)
        assertNotNull(syncJob.completedAt)
    }

    @Test
    fun `getSyncJobStatus returns job when found`() {
        val jobId = UUID.randomUUID()
        val playlist = Playlist(name = "List", createdBy = "user-1", id = UUID.randomUUID())
        val job = PlaylistSyncJob(playlist = playlist, spotifyPlaylistId = "sp-1", status = SyncJobStatus.RUNNING, id = jobId)

        every { playlistSyncJobRepository.findById(jobId) } returns Optional.of(job)

        val result = playlistService.getSyncJobStatus(jobId)

        assertEquals(job, result)
    }
}
