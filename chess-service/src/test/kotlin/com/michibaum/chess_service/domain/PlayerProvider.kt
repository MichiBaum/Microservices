package com.michibaum.chess_service.domain

import com.michibaum.chess_service.database.*

class PlayerProvider {

    companion object {
        fun player(
            account: Account? = null,
            pieceColor: PieceColor = PieceColor.WHITE,
            username: String = "someUsername",
            rating: Long = 1500
        ): Player {
            return Player(
                username = username,
                rating = rating,
                pieceColor = pieceColor,
                account = account
            )
        }
    }

}