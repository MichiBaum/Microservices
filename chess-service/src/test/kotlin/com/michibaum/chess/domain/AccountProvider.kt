package com.michibaum.chess.domain

import java.util.*

class AccountProvider {
    companion object {
        fun account(): Account {
            return Account(name = "Michi", accId = "TGsdisd3", username = "Michi1", url = "https://chess-is-best.com/Michi1", platform = ChessPlatform.CHESSCOM, person = null, createdAt = Date(1595005065053))
        }
    }
}