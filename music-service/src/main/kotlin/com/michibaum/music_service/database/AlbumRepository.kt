package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumRepository: JpaRepository<Album, UUID> {
}