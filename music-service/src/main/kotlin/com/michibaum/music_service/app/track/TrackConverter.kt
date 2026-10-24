package com.michibaum.music_service.app.track

import com.michibaum.music_service.database.Track
import org.springframework.stereotype.Component

@Component
class TrackConverter {
    fun toDto(track: Track): TrackDto =
        TrackDto(
            id = track.id.toString(),
            name = track.name,
            isrc = track.isrc
        )
}