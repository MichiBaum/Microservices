package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.app.artist.ArtistConverter
import com.michibaum.music_service.database.Playlist
import com.michibaum.music_service.database.PlaylistTrack
import com.michibaum.music_service.database.spotify.PlaylistSpotify
import org.springframework.stereotype.Component

@Component
class PlaylistConverter(
    private val artistConverter: ArtistConverter
) {

    fun toDto(playlist: Playlist, spotifyLink: PlaylistSpotify?, trackCount: Int): PlaylistDto {
        return PlaylistDto(
            id = playlist.id ?: throw IllegalStateException("Playlist ID cannot be null when converting to DTO"),
            name = playlist.name,
            description = playlist.description,
            coverImageUrl = playlist.coverImageUrl,
            isSpotify = spotifyLink != null,
            spotifyId = spotifyLink?.spotifyId,
            trackCount = trackCount,
            created = playlist.created,
            updated = playlist.updated
        )
    }

    fun toDetailsDto(
        playlist: Playlist,
        spotifyLink: PlaylistSpotify?,
        playlistTracks: List<PlaylistTrack>
    ): PlaylistDetailsDto {
        return PlaylistDetailsDto(
            id = playlist.id ?: throw IllegalStateException("Playlist ID cannot be null when converting to DTO"),
            name = playlist.name,
            description = playlist.description,
            coverImageUrl = playlist.coverImageUrl,
            isSpotify = spotifyLink != null,
            spotifyId = spotifyLink?.spotifyId,
            trackCount = playlistTracks.size,
            created = playlist.created,
            updated = playlist.updated,
            tracks = playlistTracks.map { toTrackDto(it) }
        )
    }

    fun toTrackDto(playlistTrack: PlaylistTrack): PlaylistTrackDto {
        val track = playlistTrack.track
        return PlaylistTrackDto(
            trackId = track.id ?: throw IllegalStateException("Track ID cannot be null when converting to DTO"),
            name = track.name,
            artists = track.artists.map { artistConverter.toDto(it) },
            position = playlistTrack.position,
            releaseDate = track.releaseDate
        )
    }
}
