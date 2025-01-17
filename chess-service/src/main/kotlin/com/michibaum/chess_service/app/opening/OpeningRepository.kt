package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import com.michibaum.chess_service.domain.OpeningMove
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OpeningRepository : JpaRepository<Opening, UUID>{
    fun findByLastMoveIn(moves: List<OpeningMove>): List<Opening>
}