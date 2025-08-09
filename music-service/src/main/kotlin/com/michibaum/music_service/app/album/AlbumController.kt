package com.michibaum.music_service.app.album

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AlbumController(
    private val albumService: AlbumService,
    private val albumConverter: AlbumConverter
) {
    
    @GetMapping("/api/albums/{id}")
    @PublicEndpoint
    fun getAlbum(@PathVariable id: String): ResponseEntity<AlbumDto>{
        return try {
            val uuid = UUID.fromString(id)
            val album = albumService.get(uuid)?:
                return ResponseEntity.notFound().build()
            val dto = albumConverter.toDto(album)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
    
}