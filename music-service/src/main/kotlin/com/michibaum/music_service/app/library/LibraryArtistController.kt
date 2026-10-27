package com.michibaum.music_service.app.library

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.app.artist.ArtistDto
import io.micrometer.observation.annotation.Observed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LibraryArtistController(
    private val musicLibraryService: MusicLibraryService
) {

    @Observed(name = "music.library.controller.get.artists")
    @GetMapping("/api/music/library/artists")
    fun getLibraryArtists(principal: JwtAuthentication): ResponseEntity<List<ArtistDto>> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val artists = musicLibraryService.getLibraryArtists(userId)
        return ResponseEntity.ok(artists)
    }

    @Observed(name = "music.library.controller.sync.artists")
    @PostMapping("/api/music/library/artists/sync")
    fun syncLibraryArtists(principal: JwtAuthentication): ResponseEntity<List<ArtistDto>> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val artists = musicLibraryService.syncLibraryArtists(userId)
        return ResponseEntity.ok(artists)
    }
}
