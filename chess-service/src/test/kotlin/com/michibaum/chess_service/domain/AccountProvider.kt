package com.michibaum.chess_service.domain

import java.util.*

class AccountProvider {
    companion object {
        fun account(username: String = "Michi1", person: Person? = null): Account {
            return Account(
                name = "Michi",
                accId = "TGsdisd3",
                username = username,
                url = "https://chess-is-best.com/Michi1",
                platform = ChessPlatform.CHESSCOM,
                person = person,
                createdAt = Date(1595005065053),
                games = emptySet()
            )
        }
    }
}