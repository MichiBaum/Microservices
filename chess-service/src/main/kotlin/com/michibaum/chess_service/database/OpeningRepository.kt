package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OpeningRepository : JpaRepository<Opening, UUID>{
    @Query("""
        SELECT
            o.id AS id,
            o.name AS name,
            o.deleted AS deleted,
            move.id AS lastMoveId,
            move.move AS lastMoveMove,
            move.fen AS lastMoveFen,
            parent.id AS parentMoveId
        FROM Opening o
            LEFT JOIN o.lastMove move
            LEFT JOIN move.parent parent
        WHERE o.deleted = :deleted
            AND ( (:id IS NULL AND parent IS NULL) OR parent.id = :id )
    """)
    fun findOpeningByParentMoveAndDeleted(id: UUID?, deleted: Boolean): List<OpeningWithLastMoveProjection>

    @Query("""
        SELECT
            o.id AS id,
            o.name AS name,
            o.deleted AS deleted,
            move.id AS lastMoveId,
            move.move AS lastMoveMove,
            move.fen AS lastMoveFen,
            parent.id AS parentMoveId
        FROM Opening o
            LEFT JOIN o.lastMove move
            LEFT JOIN move.parent parent
        WHERE o.deleted = :deleted
    """)
    fun findAllByDeletedFalseEagerLastMove(deleted: Boolean): List<OpeningWithLastMoveProjection>

    @Query("SELECT o FROM Opening o left join fetch o.lastMove move WHERE o.id = :id")
    fun findByIdEagerLastMove(id: UUID): Opening?
}

interface OpeningWithLastMoveProjection {
    fun getId(): UUID
    fun getName(): String
    fun isDeleted(): Boolean

    fun getLastMoveId(): UUID?
    fun getLastMoveMove(): String?
    fun getLastMoveFen(): String?

    fun getParentMoveId(): UUID?
}