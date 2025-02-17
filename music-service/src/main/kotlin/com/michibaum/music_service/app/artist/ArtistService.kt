package com.michibaum.music_service.app.artist

import com.michibaum.music_service.database.Artist
import com.michibaum.music_service.database.ArtistRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ArtistService(private val artistRepository: ArtistRepository) {

    fun find(name: String): List<Artist> =
        artistRepository.findByNameContainingIgnoreCase(name)

    fun get(uuid: UUID): Artist? {
        return artistRepository.findById(uuid).getOrNull()
    }

}