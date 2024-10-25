package com.michibaum.chess_service.domain

import com.michibaum.chess_service.apis.dtos.PieceColor
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