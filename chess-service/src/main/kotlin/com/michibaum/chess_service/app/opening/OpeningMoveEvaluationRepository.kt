package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.OpeningMoveEvaluation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OpeningMoveEvaluationRepository: JpaRepository<OpeningMoveEvaluation, UUID> {
}