package com.michibaum.music_service.app.track

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class TrackController(
    private val trackService: TrackService,
    private val trackConverter: TrackConverter
) {

    @GetMapping("/api/tracks/{id}")
    fun get(@PathVariable id: String): ResponseEntity<TrackDto> {
        return try {
            val uuid = UUID.fromString(id)
            val track = trackService.get(uuid)?:
                return ResponseEntity.notFound().build()
            val dto = trackConverter.toDto(track)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}