package com.michibaum.chess_service.app.opening

import com.michibaum.chess_service.domain.Opening
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OpeningRepository : JpaRepository<Opening, UUID>{

    @EntityGraph(attributePaths = ["lastMove", "lastMove.parent", "lastMove.moveEvaluations"])
    fun findByNameLike(name: String): List<Opening>

}