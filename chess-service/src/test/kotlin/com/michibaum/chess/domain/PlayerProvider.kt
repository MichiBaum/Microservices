package com.michibaum.chess.domain

import com.michibaum.chess.apis.dtos.PieceColor
import java.util.*

class PlayerProvider {

    companion object {
        fun player(game: Game): Player {
            return Player(
                id = UUID.randomUUID(),
                platformId = "plattformId",
                username = "someUsername",
                rating = 1459,
                pieceColor = PieceColor.WHITE,
                game = game
            )
        }
    }

}