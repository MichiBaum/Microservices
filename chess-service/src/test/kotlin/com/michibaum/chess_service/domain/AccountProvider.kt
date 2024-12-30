package com.michibaum.chess_service.domain

class AccountProvider {
    companion object {
        fun account(username: String = "Michi1", person: Person? = null): Account {
            return Account(
                name = "Michi",
                platformId = "TGsdisd3",
                username = username,
                platform = ChessPlatform.CHESSCOM,
                person = person,
                createdAt = null,
                games = emptySet()
            )
        }
    }
}