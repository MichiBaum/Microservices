package com.michibaum.music_service.database.spotify

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackSpotifyRepository: JpaRepository<TrackSpotify, UUID> {
    fun findBySpotifyId(id: String): TrackSpotify?
}