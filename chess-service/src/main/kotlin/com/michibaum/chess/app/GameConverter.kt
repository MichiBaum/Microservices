package com.michibaum.chess.app

import com.michibaum.chess.domain.Game
import org.springframework.stereotype.Component

@Component
class GameConverter {

    fun convert(game: Game): GameDto{
        return GameDto(
            id = game.id,
            chessPlatform = game.chessPlatform,
            platformId = game.platformId,
            pgn = game.pgn,
            gameType = game.gameType
        )
    }

}