package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.apis.spotify.api.user.SpotifyPlaylistApi
import com.michibaum.music_service.apis.spotify.generated.model.PlaylistTrackObject
import com.michibaum.music_service.apis.spotify.oauth.SpotifyOAuthService
import com.michibaum.music_service.database.*
import com.michibaum.music_service.database.spotify.*
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val playlistSpotifyRepository: PlaylistSpotifyRepository,
    private val playlistTrackRepository: PlaylistTrackRepository,
    private val trackRepository: TrackRepository,
    private val trackSpotifyRepository: TrackSpotifyRepository,
    private val artistRepository: ArtistRepository,
    private val artistSpotifyRepository: ArtistSpotifyRepository,
    private val playlistSpotifyApi: SpotifyPlaylistApi,
    private val spotifyOAuthService: SpotifyOAuthService,
    private val playlistConverter: PlaylistConverter
) {

    private val logger = LoggerFactory.getLogger(PlaylistService::class.java)

    @Observed(name = "music.playlist.get.user.playlists")
    @Transactional(readOnly = true)
    fun getUserPlaylists(userId: String): List<PlaylistDto> {
        val playlists = playlistRepository.findAllByOrderByCreatedDesc(userId)
        if (playlists.isEmpty()) {
            return emptyList()
        }

        val playlistIds = playlists.mapNotNull { it.id }
        val spotifyLinkMap = playlistSpotifyRepository.findByPlaylistIdIn(playlistIds)
            .associateBy { it.playlist.id }

        val countList = playlistTrackRepository.countByPlaylistIds(playlistIds)
        val trackCountMap = countList.associate {
            val pId = it[0] as UUID
            val count = (it[1] as Number).toInt()
            pId to count
        }

        return playlists.map { playlist ->
            val spotifyLink = spotifyLinkMap[playlist.id]
            val trackCount = trackCountMap[playlist.id] ?: 0
            playlistConverter.toDto(playlist, spotifyLink, trackCount)
        }
    }

    @Observed(name = "music.playlist.get.details")
    @Transactional(readOnly = true)
    fun getPlaylistDetails(id: UUID, userId: String): PlaylistDetailsDto {
        val playlist = playlistRepository.findByIdAndCreatedBy(id, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $id")

        val spotifyLink = playlistSpotifyRepository.findByPlaylistId(id)
        val playlistTracks = playlistTrackRepository.findAllByPlaylistIdWithTracks(id)

        return playlistConverter.toDetailsDto(playlist, spotifyLink, playlistTracks)
    }

    @Observed(name = "music.playlist.create")
    @Transactional
    fun createPlaylist(name: String, description: String?, coverImageUrl: String?, userId: String): PlaylistDto {
        val playlist = Playlist(
            name = name,
            description = description,
            coverImageUrl = coverImageUrl,
            createdBy = userId,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
        val savedPlaylist = playlistRepository.save(playlist)
        return playlistConverter.toDto(savedPlaylist, null, 0)
    }

    @Observed(name = "music.playlist.update")
    @Transactional
    fun updatePlaylist(
        id: UUID,
        name: String?,
        description: String?,
        coverImageUrl: String?,
        userId: String
    ): PlaylistDto {
        val playlist = playlistRepository.findByIdAndCreatedBy(id, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $id")

        if (playlistSpotifyRepository.existsByPlaylistId(id)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify playlists cannot be modified manually")
        }

        name?.let { playlist.name = it }
        description?.let { playlist.description = it }
        coverImageUrl?.let { playlist.coverImageUrl = it }
        playlist.updated = LocalDateTime.now()

        val saved = playlistRepository.save(playlist)
        val trackCount = playlistTrackRepository.countByPlaylistId(id)
        return playlistConverter.toDto(saved, null, trackCount)
    }

    @Observed(name = "music.playlist.delete")
    @Transactional
    fun deletePlaylist(id: UUID, userId: String) {
        val playlist = playlistRepository.findByIdAndCreatedBy(id, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $id")

        playlistTrackRepository.deleteByPlaylistId(id)
        playlistSpotifyRepository.deleteByPlaylistId(id)
        playlistRepository.delete(playlist)
    }

    @Observed(name = "music.playlist.add.track")
    @Transactional
    fun addTrackToPlaylist(playlistId: UUID, trackId: UUID, position: Int?, userId: String): PlaylistTrackDto {
        val playlist = playlistRepository.findByIdAndCreatedBy(playlistId, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $playlistId")

        if (playlistSpotifyRepository.existsByPlaylistId(playlistId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify playlists cannot be modified manually")
        }

        val track = trackRepository.findById(trackId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found: $trackId")
        }

        val existingTrack = playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId)
        if (existingTrack != null) {
            if (position != null) {
                existingTrack.position = position
                val updatedPt = playlistTrackRepository.save(existingTrack)
                return playlistConverter.toTrackDto(updatedPt)
            }
            return playlistConverter.toTrackDto(existingTrack)
        }

        val targetPosition = position ?: playlistTrackRepository.countByPlaylistId(playlistId)
        val newPlaylistTrack = PlaylistTrack(
            playlist = playlist,
            track = track,
            position = targetPosition,
            created = LocalDateTime.now()
        )
        val saved = playlistTrackRepository.save(newPlaylistTrack)
        return playlistConverter.toTrackDto(saved)
    }

    @Observed(name = "music.playlist.remove.track")
    @Transactional
    fun removeTrackFromPlaylist(playlistId: UUID, trackId: UUID, userId: String) {
        playlistRepository.findByIdAndCreatedBy(playlistId, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $playlistId")

        if (playlistSpotifyRepository.existsByPlaylistId(playlistId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Spotify playlists cannot be modified manually")
        }

        playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId)
    }

    @Observed(name = "music.playlist.sync.all")
    fun syncPlaylists(userId: String, requestedSpotifyPlaylistIds: List<String>? = null): List<PlaylistDto> {
        val credentials = spotifyOAuthService.getActiveToken(userId)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Spotify OAuth2 token not found for user: $userId")

        val targetSpotifyIds = if (!requestedSpotifyPlaylistIds.isNullOrEmpty()) {
            requestedSpotifyPlaylistIds
        } else {
            val userPlaylists = playlistSpotifyApi.myPlaylists(credentials)
            userPlaylists.mapNotNull { it.id }
        }

        for (spotifyId in targetSpotifyIds) {
            try {
                syncSingleSpotifyPlaylist(spotifyId, credentials, userId)
            } catch (e: Exception) {
                logger.error("Failed to sync Spotify playlist $spotifyId for user $userId", e)
            }
        }

        return getUserPlaylists(userId)
    }

    @Observed(name = "music.playlist.sync.by.id")
    fun syncPlaylistById(id: UUID, userId: String): PlaylistDetailsDto {
        val playlist = playlistRepository.findByIdAndCreatedBy(id, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found: $id")

        val spotifyLink = playlistSpotifyRepository.findByPlaylistId(id)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist $id is not linked to Spotify")

        val credentials = spotifyOAuthService.getActiveToken(userId)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Spotify OAuth2 token not found for user: $userId")

        syncSingleSpotifyPlaylist(spotifyLink.spotifyId, credentials, userId)
        return getPlaylistDetails(id, userId)
    }

    private fun syncSingleSpotifyPlaylist(
        spotifyPlaylistId: String,
        credentials: SpotifyOAuthCredentials,
        userId: String
    ) {
        val spotifyPlaylist = playlistSpotifyApi.getPlaylist(spotifyPlaylistId, credentials)
            ?: return

        val rawTracks = playlistSpotifyApi.getPlaylistTracks(spotifyPlaylistId, credentials)
        val parsedTracks = extractTracks(rawTracks)

        persistSyncedPlaylist(
            spotifyPlaylistId = spotifyPlaylistId,
            name = spotifyPlaylist.name ?: "Spotify Playlist",
            description = spotifyPlaylist.description,
            coverImageUrl = spotifyPlaylist.images?.firstOrNull()?.url,
            tracks = parsedTracks,
            userId = userId
        )
    }

    @Transactional
    fun persistSyncedPlaylist(
        spotifyPlaylistId: String,
        name: String,
        description: String?,
        coverImageUrl: String?,
        tracks: List<ParsedTrackData>,
        userId: String
    ): Playlist {
        val existingLink = playlistSpotifyRepository.findBySpotifyIdAndUserId(spotifyPlaylistId, userId)
        val playlist = if (existingLink != null) {
            val existingPlaylist = existingLink.playlist
            existingPlaylist.name = name
            existingPlaylist.description = description
            existingPlaylist.coverImageUrl = coverImageUrl
            existingPlaylist.updated = LocalDateTime.now()
            existingLink.updated = Instant.now()
            playlistSpotifyRepository.save(existingLink)
            playlistRepository.save(existingPlaylist)
        } else {
            val newPlaylist = Playlist(
                name = name,
                description = description,
                coverImageUrl = coverImageUrl,
                createdBy = userId,
                created = LocalDateTime.now(),
                updated = LocalDateTime.now()
            )
            val savedPlaylist = playlistRepository.save(newPlaylist)
            val newLink = PlaylistSpotify(
                playlist = savedPlaylist,
                spotifyId = spotifyPlaylistId,
                created = Instant.now(),
                updated = Instant.now()
            )
            playlistSpotifyRepository.save(newLink)
            savedPlaylist
        }

        val resolvedTracks = tracks.map { trackData ->
            resolveOrCreateTrack(trackData)
        }

        playlistTrackRepository.deleteByPlaylistId(playlist.id!!)
        playlistTrackRepository.flush()

        val uniqueTracks = resolvedTracks.distinctBy { it.id }
        val playlistTracks = uniqueTracks.mapIndexed { index, track ->
            PlaylistTrack(
                playlist = playlist,
                track = track,
                position = index,
                created = LocalDateTime.now()
            )
        }
        playlistTrackRepository.saveAll(playlistTracks)

        return playlist
    }

    private fun resolveOrCreateTrack(trackData: ParsedTrackData): Track {
        val existingTrackSpotify = trackSpotifyRepository.findBySpotifyId(trackData.spotifyTrackId)
        if (existingTrackSpotify != null) {
            return existingTrackSpotify.track
        }

        val resolvedArtists = trackData.artists.map { artistData ->
            resolveOrCreateArtist(artistData)
        }.toMutableSet()

        val newTrack = Track(
            name = trackData.name,
            artists = resolvedArtists,
            releaseDate = trackData.releaseDate,
            isrc = trackData.isrc?.let { setOf(it) } ?: emptySet(),
            created = Instant.now(),
            updated = Instant.now()
        )
        val savedTrack = trackRepository.save(newTrack)

        val newTrackSpotify = TrackSpotify(
            track = savedTrack,
            spotifyId = trackData.spotifyTrackId,
            created = Instant.now(),
            updated = Instant.now()
        )
        trackSpotifyRepository.save(newTrackSpotify)

        return savedTrack
    }

    private fun resolveOrCreateArtist(artistData: ParsedArtistData): Artist {
        if (!artistData.spotifyArtistId.isNullOrBlank()) {
            val existingArtistSpotify = artistSpotifyRepository.findBySpotifyId(artistData.spotifyArtistId)
            if (existingArtistSpotify != null) {
                return existingArtistSpotify.artist
            }

            val newArtist = artistRepository.save(
                Artist(
                    name = artistData.name,
                    isni = null,
                    created = LocalDateTime.now(),
                    updated = LocalDateTime.now()
                )
            )
            artistSpotifyRepository.save(
                ArtistSpotify(
                    artist = newArtist,
                    spotifyId = artistData.spotifyArtistId
                )
            )
            return newArtist
        }

        val existingArtists = artistRepository.findByNameContainingIgnoreCase(artistData.name)
        val matchedArtist = existingArtists.firstOrNull { it.name.equals(artistData.name, ignoreCase = true) }
        if (matchedArtist != null) {
            return matchedArtist
        }

        return artistRepository.save(
            Artist(
                name = artistData.name,
                isni = null,
                created = LocalDateTime.now(),
                updated = LocalDateTime.now()
            )
        )
    }

    private fun extractTracks(rawTracks: List<PlaylistTrackObject>): List<ParsedTrackData> {
        return rawTracks.mapNotNull { obj ->
            val spotifyTrackId = obj.item?.id ?: obj.track?.id ?: return@mapNotNull null
            val name = obj.item?.name ?: obj.track?.name ?: return@mapNotNull null

            val rawArtists = obj.item?.artists ?: obj.track?.artists ?: emptyList()
            val artists = rawArtists.map { artistObj ->
                ParsedArtistData(
                    spotifyArtistId = artistObj.id,
                    name = artistObj.name ?: "Unknown Artist"
                )
            }

            val rawReleaseDate = obj.item?.album?.releaseDate ?: obj.track?.album?.releaseDate
            val isrc = obj.item?.externalIds?.isrc ?: obj.track?.externalIds?.isrc

            ParsedTrackData(
                spotifyTrackId = spotifyTrackId,
                name = name,
                artists = artists,
                releaseDate = parseReleaseDate(rawReleaseDate),
                isrc = isrc
            )
        }
    }

    private fun parseReleaseDate(rawDate: String?): LocalDate? {
        if (rawDate.isNullOrBlank()) return null
        return try {
            when {
                rawDate.length == 10 -> LocalDate.parse(rawDate)
                rawDate.length == 7 -> LocalDate.parse("$rawDate-01")
                rawDate.length == 4 -> LocalDate.parse("$rawDate-01-01")
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    data class ParsedTrackData(
        val spotifyTrackId: String,
        val name: String,
        val artists: List<ParsedArtistData>,
        val releaseDate: LocalDate?,
        val isrc: String?
    )

    data class ParsedArtistData(
        val spotifyArtistId: String?,
        val name: String
    )
}
