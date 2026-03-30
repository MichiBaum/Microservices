package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.database.Game
import org.springframework.stereotype.Component

@Component
class GameConverter {

    fun convert(game: Game): GameResponseDto {
        return GameResponseDto(
            id = game.idOrThrow(),
            chessPlatform = game.chessPlatform,
            platformId = game.platformId,
            pgn = game.pgn,
            gameType = game.gameType
        )
    }

}