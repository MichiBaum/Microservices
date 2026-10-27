package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserLibraryArtistRepository : JpaRepository<UserLibraryArtist, UUID> {
    @EntityGraph(attributePaths = ["artist"])
    fun findByUserId(userId: String): List<UserLibraryArtist>
    fun existsByUserIdAndArtistId(userId: String, artistId: UUID): Boolean
    fun findByUserIdAndArtistId(userId: String, artistId: UUID): UserLibraryArtist?
}
