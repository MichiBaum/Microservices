package com.michibaum.chess_service.app.chessengine

import com.michibaum.chess_service.domain.ChessEngine
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ChessEngineRepository: JpaRepository<ChessEngine, UUID> {
}