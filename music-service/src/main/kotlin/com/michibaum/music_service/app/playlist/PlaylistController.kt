package com.michibaum.music_service.app.playlist

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import io.micrometer.observation.annotation.Observed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping(value = ["/api/playlists", "/api/music/library/playlists"])
class PlaylistController(
    private val playlistService: PlaylistService
) {

    @Observed(name = "music.playlist.controller.get.all")
    @GetMapping
    fun getPlaylists(principal: JwtAuthentication): ResponseEntity<List<PlaylistDto>> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val playlists = playlistService.getUserPlaylists(userId)
        return ResponseEntity.ok(playlists)
    }

    @Observed(name = "music.playlist.controller.create")
    @PostMapping
    fun createPlaylist(
        principal: JwtAuthentication,
        @RequestBody request: CreatePlaylistRequest
    ): ResponseEntity<PlaylistDto> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val created = playlistService.createPlaylist(
            name = request.name,
            description = request.description,
            coverImageUrl = request.coverImageUrl,
            userId = userId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @Observed(name = "music.playlist.controller.get.by.id")
    @GetMapping("/{id}")
    fun getPlaylist(
        principal: JwtAuthentication,
        @PathVariable id: UUID
    ): ResponseEntity<PlaylistDetailsDto> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val details = playlistService.getPlaylistDetails(id, userId)
        return ResponseEntity.ok(details)
    }

    @Observed(name = "music.playlist.controller.update")
    @PutMapping("/{id}")
    fun updatePlaylist(
        principal: JwtAuthentication,
        @PathVariable id: UUID,
        @RequestBody request: UpdatePlaylistRequest
    ): ResponseEntity<PlaylistDto> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val updated = playlistService.updatePlaylist(
            id = id,
            name = request.name,
            description = request.description,
            coverImageUrl = request.coverImageUrl,
            userId = userId
        )
        return ResponseEntity.ok(updated)
    }

    @Observed(name = "music.playlist.controller.delete")
    @DeleteMapping("/{id}")
    fun deletePlaylist(
        principal: JwtAuthentication,
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        playlistService.deletePlaylist(id, userId)
        return ResponseEntity.noContent().build()
    }

    @Observed(name = "music.playlist.controller.add.track")
    @PostMapping("/{id}/tracks")
    fun addTrack(
        principal: JwtAuthentication,
        @PathVariable id: UUID,
        @RequestBody request: AddTrackRequest
    ): ResponseEntity<PlaylistTrackDto> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val track = playlistService.addTrackToPlaylist(
            playlistId = id,
            trackId = request.trackId,
            position = request.position,
            userId = userId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(track)
    }

    @Observed(name = "music.playlist.controller.remove.track")
    @DeleteMapping("/{id}/tracks/{trackId}")
    fun removeTrack(
        principal: JwtAuthentication,
        @PathVariable id: UUID,
        @PathVariable trackId: UUID
    ): ResponseEntity<Void> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        playlistService.removeTrackFromPlaylist(id, trackId, userId)
        return ResponseEntity.noContent().build()
    }

    @Observed(name = "music.playlist.controller.sync.all")
    @PostMapping("/sync")
    fun syncPlaylists(
        principal: JwtAuthentication,
        @RequestBody(required = false) request: SyncPlaylistsRequest?
    ): ResponseEntity<List<PlaylistDto>> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val playlists = playlistService.syncPlaylists(userId, request?.spotifyPlaylistIds)
        return ResponseEntity.ok(playlists)
    }

    @Observed(name = "music.playlist.controller.sync.by.id")
    @PostMapping("/{id}/sync")
    fun syncPlaylistById(
        principal: JwtAuthentication,
        @PathVariable id: UUID
    ): ResponseEntity<PlaylistDetailsDto> {
        val userId = principal.getUserId()
        if (userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val details = playlistService.syncPlaylistById(id, userId)
        return ResponseEntity.ok(details)
    }
}
