package com.michibaum.chess_service.app.game_move

import com.michibaum.chess_service.database.Game
import com.michibaum.chess_service.database.GameMove
import com.michibaum.chess_service.database.GameMoveRepository
import org.springframework.stereotype.Service

@Service
class GameMoveService(
    private val gameMoveRepository: GameMoveRepository
) {
    fun findByGame(game: Game): List<GameMove> =
        gameMoveRepository.findByGameOrderByMoveNumberAscIsWhiteDesc(game)
}