package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GameMoveRepository : JpaRepository<GameMove, UUID> {
    @Query("""
        select gm from GameMove gm
        where gm.game.id = :gameId
        order by gm.moveNumber asc, gm.isWhite desc
    """)
    fun findByGameOrderByMoveNumberAscIsWhiteDesc(game: Game): List<GameMove>
}
