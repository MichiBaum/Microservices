package com.michibaum.chess_service.domain

import com.michibaum.chess_service.apis.dtos.PieceColor
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.Game
import com.michibaum.chess_service.database.Player

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