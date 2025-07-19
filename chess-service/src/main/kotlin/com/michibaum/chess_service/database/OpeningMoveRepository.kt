package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OpeningMoveRepository: JpaRepository<OpeningMove, UUID> {
    fun findAllByParentIdAndDeletedFalse(parent: UUID?): List<OpeningMove>

    @Query(value = """
        WITH RECURSIVE move_hierarchy AS (
            SELECT id, move, fen, parent_id
            FROM opening_move
            WHERE id = :startingId
            
            UNION ALL
            
            SELECT move.id, move.move, move.fen, move.parent_id
            FROM opening_move move
            INNER JOIN move_hierarchy hierarchy ON move.id = hierarchy.parent_id
        )
        SELECT
            hierarchy.id AS moveId,
            hierarchy.move AS move,
            hierarchy.fen AS fen,
            hierarchy.parent_id AS parentId,
            evaluation.engine_id AS engineId,
            evaluation.depth AS depth,
            evaluation.id AS evaluationId,
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
            SELECT id, move, fen, parent_id, 0 AS depth
            FROM opening_move
            WHERE id = :startingId AND deleted = false
    
            UNION ALL
    
            SELECT move.id, move.move, move.fen, move.parent_id, hierarchy.depth + 1
            FROM opening_move move
            INNER JOIN move_hierarchy hierarchy ON move.parent_id = hierarchy.id
            WHERE hierarchy.depth + 1 <= :maxDepth AND move.deleted = false
        )
        SELECT
            hierarchy.id AS moveId,
            hierarchy.move AS move,
            hierarchy.fen AS fen,
            hierarchy.parent_id AS parentId,
            evaluation.engine_id AS engineId,
            evaluation.depth AS depth,
            evaluation.id AS evaluationId,
            evaluation.evaluation AS evaluation,
            opening.name AS openingName,
            opening.id AS openingId
        FROM move_hierarchy hierarchy
        LEFT JOIN opening_move_evaluation evaluation ON hierarchy.id = evaluation.opening_move_id
        LEFT JOIN opening ON hierarchy.id = opening.last_move_id AND opening.deleted = false
    """, nativeQuery = true)
    fun findMoveChildren(@Param("startingId") startingId: UUID?, @Param("maxDepth") maxDepth: Int): List<MoveHierarchyProjection>

    @Query("""
        SELECT o FROM OpeningMove o 
            left join fetch o.moveEvaluations e
            left join fetch e.engine
        WHERE o.id = :uuid""")
    fun findByIdEagerEvaluations(uuid: UUID): OpeningMove?
}