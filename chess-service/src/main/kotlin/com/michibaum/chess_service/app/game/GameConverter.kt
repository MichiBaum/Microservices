package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.domain.Game
import org.springframework.stereotype.Component

@Component
class GameConverter {

    fun convert(game: Game): GameDto {
        return GameDto(
            id = game.idOrThrow(),
            chessPlatform = game.chessPlatform,
            platformId = game.platformId,
            pgn = game.pgn,
            gameType = game.gameType
        )
    }

}