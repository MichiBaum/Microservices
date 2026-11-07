package com.michibaum.music_service.app.artist

import com.michibaum.music_service.database.Artist
import org.springframework.stereotype.Component

@Component
class ArtistConverter {

    fun toDto(artist: Artist): ArtistDto =
        ArtistDto(
            id = artist.id.toString(),
            name = artist.name,
            isni = artist.isni
        )

}