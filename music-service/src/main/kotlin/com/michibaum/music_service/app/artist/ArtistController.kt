package com.michibaum.music_service.app.artist

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ArtistController(
    private val artistService: ArtistService,
    private val artistConverter: ArtistConverter
) {

    @PublicEndpoint
    @GetMapping("/api/artists")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun searchArtist(@RequestParam name: String): List<ArtistDto> {
        return artistService.find(name)
            .map { artist -> artistConverter.toDto(artist) }
    }

    @PublicEndpoint
    @GetMapping("/api/artists/{id}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun get(@PathVariable id: String): ResponseEntity<ArtistDto>{
        return try {
            val uuid = UUID.fromString(id)
            val artist = artistService.get(uuid)?:
                return ResponseEntity.notFound().build()
            val dto = artistConverter.toDto(artist)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}