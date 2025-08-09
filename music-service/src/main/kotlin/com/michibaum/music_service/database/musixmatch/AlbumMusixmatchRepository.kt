package com.michibaum.music_service.database.musixmatch

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumMusixmatchRepository: JpaRepository<AlbumMusixmatch, UUID> {
    fun findByMusixmatchId(id: String): AlbumMusixmatch?
}