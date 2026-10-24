package com.michibaum.music_service.app.album

import com.michibaum.music_service.database.Album
import org.springframework.stereotype.Component

@Component
class AlbumConverter {
    
    fun toDto(album: Album): AlbumDto {
        return AlbumDto(album.name)
    }
    
}