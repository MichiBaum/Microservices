package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OpeningMoveEvaluationRepository: JpaRepository<OpeningMoveEvaluation, UUID> {
}