package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OpeningRepository : JpaRepository<Opening, UUID>{
    @Query("""
        SELECT o FROM Opening o 
        left join fetch o.lastMove move 
        left join fetch move.parent parent 
        WHERE o.deleted = :deleted
        AND ( (:id IS NULL AND parent IS NULL) OR parent.id = :id )
    """)
    fun findOpeningByParentMoveAndDeleted(id: UUID?, deleted: Boolean): List<Opening>

    @Query("""
        SELECT o FROM Opening o
        left join fetch o.lastMove move
        left join fetch move.parent parent 
        WHERE o.deleted = :deleted
    """)
    fun findAllByDeletedFalseEagerLastMove(deleted: Boolean): List<Opening>

    @Query("SELECT o FROM Opening o left join fetch o.lastMove move WHERE o.id = :id")
    fun findByIdEagerLastMove(id: UUID): Opening?
}