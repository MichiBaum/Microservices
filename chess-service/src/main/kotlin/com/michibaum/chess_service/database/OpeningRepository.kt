package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OpeningRepository : JpaRepository<Opening, UUID>{
    fun findByLastMoveIn(moves: List<OpeningMove>): List<Opening>
}