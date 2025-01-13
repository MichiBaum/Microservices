package com.michibaum.chess_service.domain

import com.michibaum.chess_service.apis.dtos.PieceColor

class PlayerProvider {

    companion object {
        fun player(account: Account, game: Game): Player {
            return Player(
                username = "someUsername",
                rating = 1459,
                pieceColor = PieceColor.WHITE,
                game = game,
                account = account,
            )
        }
    }

}