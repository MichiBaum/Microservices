package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.OpeningMove
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OpeningMoveRepository: JpaRepository<OpeningMove, UUID> {
    fun findAllByParent(parent: OpeningMove?): List<OpeningMove>

    @Query(value = """
        WITH RECURSIVE move_hierarchy AS (
            SELECT id, move, parent_id
            FROM
                opening_move
            WHERE
                id = :startingId
            
            UNION ALL
            
            SELECT om.id, om.move, om.parent_id
            FROM
                opening_move om
            INNER JOIN move_hierarchy mh
                ON om.id = mh.parent_id
        )
        SELECT
            mh.id AS moveId,
            mh.move AS move,
            mh.parent_id AS parentId,
            ome.engine_id AS engineId,
            ome.depth AS depth,
            ome.evaluation AS evaluation
        FROM
            move_hierarchy mh
        LEFT JOIN
            opening_move_evaluation ome
        ON
            mh.id = ome.opening_move_id
        """, nativeQuery = true)
    fun findMoveHierarchy(@Param("startingId") startingId: UUID): List<MoveHierarchyProjection>
}