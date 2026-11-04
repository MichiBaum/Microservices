package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlaylistRepository : JpaRepository<Playlist, UUID> {
    fun findByCreatedBy(userId: String): List<Playlist>

    @Query("SELECT p FROM Playlist p WHERE p.createdBy = :userId ORDER BY p.created DESC")
    fun findAllByOrderByCreatedDesc(userId: String): List<Playlist>

    @Query("SELECT p FROM Playlist p WHERE p.id = :id AND p.createdBy = :userId")
    fun findByIdAndCreatedBy(id: UUID, userId: String): Playlist?

    fun existsByCreatedBy(userId: String): Boolean
}
