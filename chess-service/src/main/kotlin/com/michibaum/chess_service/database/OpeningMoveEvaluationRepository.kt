package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

@Repository
interface OpeningMoveEvaluationRepository: JpaRepository<OpeningMoveEvaluation, UUID> {
    
    @Query("SELECT ome FROM OpeningMoveEvaluation ome WHERE ome.openingMove.id = :uuid")
    fun findByMoveId(uuid: UUID): List<OpeningMoveEvaluation>
}