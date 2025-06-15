package com.michibaum.alexandria_service.database.note

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface NoteRepository : JpaRepository<Note, UUID> {
    
    @Query("""
        SELECT n FROM Note n
        WHERE n.belongsTo = :belongsTo
    """)
    fun findAllByBelongsTo(belongsTo: String): List<Note>

}