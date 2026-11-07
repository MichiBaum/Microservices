package com.michibaum.music_service.app.playlist

import java.time.LocalDateTime
import java.util.UUID

data class PlaylistDetailsDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val coverImageUrl: String?,
    val isSpotify: Boolean,
    val spotifyId: String?,
    val trackCount: Int,
    val created: LocalDateTime?,
    val updated: LocalDateTime?,
    val tracks: List<PlaylistTrackDto>
)
