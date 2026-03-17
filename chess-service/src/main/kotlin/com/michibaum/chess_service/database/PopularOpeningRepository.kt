package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PopularOpeningRepository : JpaRepository<PopularOpening, UUID> {

    @Query("""
        SELECT 
            po.id AS id,
            po.ranking AS ranking,
            o.id AS openingId,
            o.name AS openingName,
            lm.id AS lastMoveId,
            lm.move AS lastMoveMove,
            lm.fen AS lastMoveFen
        FROM PopularOpening po
            left join po.opening o
            left join o.lastMove lm
        ORDER BY po.ranking ASC
    """
    )
    fun findAllProjectionOrderByRankingAsc(): List<PopularOpeningMoveProjection>
    
}

interface PopularOpeningMoveProjection {

    fun getId(): UUID
    fun getRanking(): Int

    fun getOpeningId(): UUID
    fun getOpeningName(): String

    fun getLastMoveId(): UUID
    fun getLastMoveMove(): String
    fun getLastMoveFen(): String

}