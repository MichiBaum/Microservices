package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.apis.spotify.api.user.SpotifyPlaylistApi
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.database.Playlist
import com.michibaum.music_service.database.PlaylistRepository
import com.michibaum.music_service.database.PlaylistSyncJob
import com.michibaum.music_service.database.PlaylistSyncJobRepository
import com.michibaum.music_service.database.PlaylistTrack
import com.michibaum.music_service.database.PlaylistTrackRepository
import com.michibaum.music_service.database.SyncJobStatus
import com.michibaum.music_service.database.TrackRepository
import com.michibaum.music_service.database.spotify.PlaylistSpotify
import com.michibaum.music_service.database.spotify.PlaylistSpotifyRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Service
class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val playlistSpotifyRepository: PlaylistSpotifyRepository,
    private val playlistSyncJobRepository: PlaylistSyncJobRepository,
    private val playlistTrackRepository: PlaylistTrackRepository,
    private val trackRepository: TrackRepository,
    private val playlistSpotifyApi: SpotifyPlaylistApi,
    private val spotifyOAuthService: SpotifyOAuthService
) {

    @Transactional
    fun createPlaylist(name: String, description: String?, coverImageUrl: String?, userId: String): Playlist {
        val playlist = Playlist(
            name = name,
            description = description,
            coverImageUrl = coverImageUrl,
            createdBy = userId,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )

        return playlistRepository.save(playlist)
    }

    @Transactional(readOnly = true)
    fun getPlaylist(id: UUID, userId: String): Playlist {
        return playlistRepository.findByIdAndCreatedBy(id, userId)
            ?: throw RuntimeException("Playlist not found: $id")
    }

    @Transactional
    fun updatePlaylist(id: UUID, name: String?, description: String?, coverImageUrl: String?, userId: String): Playlist {
        val playlist = getPlaylist(id, userId)
        playlist.name = name ?: playlist.name
        playlist.description = description ?: playlist.description
        playlist.coverImageUrl = coverImageUrl ?: playlist.coverImageUrl
        playlist.updated = LocalDateTime.now()
        return playlistRepository.save(playlist)
    }

    @Transactional
    fun deletePlaylist(id: UUID, userId: String) {
        val playlist = getPlaylist(id, userId)
        val playlistId = playlist.id ?: id
        deletePlaylistTracks(playlistId)
        playlistSpotifyRepository.deleteByPlaylistId(playlistId)
        playlistSyncJobRepository.deleteByPlaylistId(playlistId)
        playlistRepository.delete(playlist)
    }

    @Transactional
    fun addTrackToPlaylist(playlistId: UUID, trackId: UUID, position: Int, userId: String): PlaylistTrack {
        val playlist = getPlaylist(playlistId, userId)
        val existingTrack = playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId)

        if (existingTrack != null) {
            existingTrack.position = position
            return playlistTrackRepository.save(existingTrack)
        }

        val track = trackRepository.findById(trackId).orElseThrow {
            RuntimeException("Track not found: $trackId")
        }

        val newTrack = PlaylistTrack(
            playlist = playlist,
            track = track,
            position = position,
            created = LocalDateTime.now()
        )
        return playlistTrackRepository.save(newTrack)
    }

    @Transactional
    fun removeTrackFromPlaylist(playlistId: UUID, trackId: UUID, userId: String) {
        getPlaylist(playlistId, userId)
        playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId)
    }

    @Transactional(readOnly = true)
    fun getPlaylistTracks(playlistId: UUID, userId: String): List<PlaylistTrack> {
        getPlaylist(playlistId, userId)
        return playlistTrackRepository.findByPlaylistId(playlistId)
            .sortedBy { it.position }
    }

    @Transactional
    fun syncPlaylistsFromSpotify(spotifyPlaylistIds: List<String>, userId: String): UUID {
        if (spotifyPlaylistIds.isEmpty()) {
            throw IllegalArgumentException("At least one Spotify playlist ID must be provided")
        }

        val spotifyCredentials = spotifyOAuthService.getActiveToken(userId)
            ?: throw RuntimeException("Spotify OAuth2 token not found for user: $userId")

        val playlist = Playlist(
            name = "Imported from Spotify",
            description = null,
            coverImageUrl = null,
            createdBy = userId,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        val savedPlaylist = playlistRepository.save(playlist)

        val syncJob = PlaylistSyncJob(
            playlist = savedPlaylist,
            spotifyPlaylistId = spotifyPlaylistIds.first(),
            status = SyncJobStatus.PENDING,
            startedAt = Instant.now(),
            created = Instant.now()
        )
        val savedJob = playlistSyncJobRepository.save(syncJob)

        val playlistId = savedPlaylist.id!!
        val runSync = {
            processPlaylistSyncs(playlistId, spotifyPlaylistIds, spotifyCredentials)
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    CompletableFuture.runAsync { runSync() }
                }
            })
        } else {
            CompletableFuture.runAsync { runSync() }
        }

        return savedJob.id ?: playlistId
    }

    @Async
    fun processPlaylistSyncs(playlistId: UUID, spotifyPlaylistIds: List<String>, credentials: SpotifyOAuthCredentials) {
        var totalTracksAdded = 0
        var duplicateCount = 0
        var errorCount = 0

        val initialJob = playlistSyncJobRepository.findById(playlistId).orElse(null)
            ?: playlistSyncJobRepository.findByPlaylistId(playlistId).firstOrNull()
        initialJob?.let {
            it.status = SyncJobStatus.RUNNING
            playlistSyncJobRepository.save(it)
        }

        try {
            for (spotifyPlaylistId in spotifyPlaylistIds) {
                val spotifyPlaylist = playlistSpotifyApi.getPlaylist(spotifyPlaylistId, credentials) ?: continue

                val playlist = playlistRepository.findById(playlistId).orElse(null)
                if (playlist != null) {
                    spotifyPlaylist.name?.let { playlist.name = it }
                    spotifyPlaylist.description?.let { playlist.description = it }
                    spotifyPlaylist.images?.firstOrNull()?.url?.let { playlist.coverImageUrl = it }
                    playlist.updated = LocalDateTime.now()
                    playlistRepository.save(playlist)

                    if (playlistSpotifyRepository.findBySpotifyId(spotifyPlaylistId) == null) {
                        playlistSpotifyRepository.save(
                            PlaylistSpotify(
                                playlist = playlist,
                                spotifyId = spotifyPlaylistId
                            )
                        )
                    }
                }

                val spotifyTracks = playlistSpotifyApi.getPlaylistTracks(spotifyPlaylistId, credentials)

                for ((index, spotifyTrack) in spotifyTracks.withIndex()) {
                    try {
                        val currentPlaylist = playlist ?: playlistRepository.findById(playlistId).orElse(null) ?: break

                        val trackUri = spotifyTrack.track?.uri ?: spotifyTrack.item?.uri
                        val trackId = spotifyTrack.track?.id ?: spotifyTrack.item?.id
                        val trackName = spotifyTrack.track?.name ?: spotifyTrack.item?.name
                        val artistName = spotifyTrack.track?.artists?.firstOrNull()?.name
                            ?: spotifyTrack.item?.artists?.firstOrNull()?.name

                        val track = (trackId?.let { trackRepository.findBySpotifyId(it) })
                            ?: (trackUri?.let { trackRepository.findBySpotifyId(it) })
                            ?: (if (trackName != null && artistName != null) trackRepository.findByNameAndArtist(trackName, artistName) else null)
                            ?: (trackName?.let { trackRepository.findByNameAndArtist(it) })

                        val resolvedTrackId = track?.id
                        if (track != null && resolvedTrackId != null) {
                            val existing = playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, resolvedTrackId)
                            if (existing != null) {
                                existing.position = index
                                playlistTrackRepository.save(existing)
                                duplicateCount++
                            } else {
                                playlistTrackRepository.save(
                                    PlaylistTrack(
                                        playlist = currentPlaylist,
                                        track = track,
                                        position = index,
                                        created = LocalDateTime.now()
                                    )
                                )
                                totalTracksAdded++
                            }
                        } else {
                            duplicateCount++
                        }
                    } catch (e: Exception) {
                        errorCount++
                        logger.error("Error adding track from Spotify playlist: ${e.message}", e)
                    }
                }
            }

            val job = playlistSyncJobRepository.findById(playlistId).orElse(null)
                ?: playlistSyncJobRepository.findByPlaylistId(playlistId).firstOrNull()
            job?.let {
                it.status = SyncJobStatus.COMPLETED
                it.completedAt = Instant.now()
                it.trackCount = totalTracksAdded
                playlistSyncJobRepository.save(it)
            }

        } catch (e: Exception) {
            logger.error("Error syncing playlists from Spotify", e)

            val job = playlistSyncJobRepository.findById(playlistId).orElse(null)
                ?: playlistSyncJobRepository.findByPlaylistId(playlistId).firstOrNull()
            job?.let {
                it.status = SyncJobStatus.FAILED
                it.completedAt = Instant.now()
                it.errorMessage = e.message
                it.trackCount = totalTracksAdded
                playlistSyncJobRepository.save(it)
            }
        }
    }

    @Transactional(readOnly = true)
    fun getSyncJobStatus(jobId: UUID): PlaylistSyncJob {
        return playlistSyncJobRepository.findById(jobId).orElse(null)
            ?: playlistSyncJobRepository.findByPlaylistId(jobId).firstOrNull()
            ?: throw RuntimeException("Sync job not found: $jobId")
    }

    private fun deletePlaylistTracks(playlistId: UUID) {
        playlistTrackRepository.deleteByPlaylistId(playlistId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PlaylistService::class.java)
    }
}
