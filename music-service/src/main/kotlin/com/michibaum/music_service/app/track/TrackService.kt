package com.michibaum.music_service.app.track

import com.michibaum.music_service.database.Track
import com.michibaum.music_service.database.TrackRepository
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class TrackService(
    private val trackRepository: TrackRepository
) {

    fun get(id: UUID): Track? =
        trackRepository.findById(id).getOrNull()

}