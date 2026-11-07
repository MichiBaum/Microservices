package com.michibaum.music_service.app.playlist

import com.michibaum.music_service.app.artist.ArtistDto
import java.time.LocalDate
import java.util.UUID

data class PlaylistTrackDto(
    val trackId: UUID,
    val name: String,
    val artists: List<ArtistDto>,
    val position: Int,
    val releaseDate: LocalDate?
)
