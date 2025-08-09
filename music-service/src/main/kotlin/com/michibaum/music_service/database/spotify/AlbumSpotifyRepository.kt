package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumSpotifyRepository: JpaRepository<AlbumSpotify, UUID> {
    fun findBySpotifyId(id: String): AlbumSpotify?
}