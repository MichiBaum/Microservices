package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OpeningMoveRepository: JpaRepository<OpeningMove, UUID> {
    fun findAllByParentId(parent: UUID?): List<OpeningMove>

    @Query(value = """
        WITH RECURSIVE move_hierarchy AS (
            SELECT id, move, parent_id
            FROM opening_move
            WHERE id = :startingId
            
            UNION ALL
            
            SELECT move.id, move.move, move.parent_id
            FROM opening_move move
            INNER JOIN move_hierarchy hierarchy ON move.id = hierarchy.parent_id
        )
        SELECT
            hierarchy.id AS moveId,
            hierarchy.move AS move,
            hierarchy.parent_id AS parentId,
            evaluation.engine_id AS engineId,
            evaluation.depth AS depth,
            evaluation.evaluation AS evaluation,
            opening.name AS openingName,
            opening.id AS openingId
        FROM move_hierarchy hierarchy
        LEFT JOIN opening_move_evaluation evaluation ON hierarchy.id = evaluation.opening_move_id
        LEFT JOIN opening ON hierarchy.id = opening.last_move_id
        """, nativeQuery = true)
    fun findMoveHirarchyBefore(@Param("startingId") startingId: UUID): List<MoveHierarchyProjection>

    @Query(value = """
        WITH RECURSIVE move_hierarchy AS (
            SELECT id, move, parent_id
            FROM opening_move
            WHERE id = :startingId
    
            UNION ALL
    
            SELECT move.id, move.move, move.parent_id
            FROM opening_move move
            INNER JOIN move_hierarchy hierarchy ON move.parent_id = hierarchy.id
        )
        SELECT
            hierarchy.id AS moveId,
            hierarchy.move AS move,
            hierarchy.parent_id AS parentId,
            evaluation.engine_id AS engineId,
            evaluation.depth AS depth,
            evaluation.evaluation AS evaluation,
            opening.name AS openingName,
            opening.id AS openingId
        FROM move_hierarchy hierarchy
        LEFT JOIN opening_move_evaluation evaluation ON hierarchy.id = evaluation.opening_move_id
        LEFT JOIN opening ON hierarchy.id = opening.last_move_id;
    """, nativeQuery = true)
    fun findMoveChildren(@Param("startingId") startingId: UUID?): List<MoveHierarchyProjection>
}