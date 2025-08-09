package com.michibaum.music_service.app.album

import com.michibaum.music_service.database.Album
import com.michibaum.music_service.database.AlbumRepository
import jakarta.persistence.Id
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class AlbumService(
    private val albumRepository: AlbumRepository
) {
    
    fun get(id: UUID): Album? {
        return albumRepository.findById(id).getOrNull()
    }
    
}