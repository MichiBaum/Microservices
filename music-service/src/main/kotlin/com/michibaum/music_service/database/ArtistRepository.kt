package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistRepository: JpaRepository<Artist, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<Artist>
}