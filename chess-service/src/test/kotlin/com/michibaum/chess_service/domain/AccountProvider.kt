package com.michibaum.chess_service.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AccountProvider {
    companion object {
        fun account(username: String = "Michi1", person: Person? = null): Account {
            return Account(
                name = "Michi",
                platformId = "TGsdisd3",
                username = username,
                url = "https://chess-is-best.com/Michi1",
                platform = ChessPlatform.CHESSCOM,
                person = person,
                createdAt = LocalDate.ofInstant(Instant.ofEpochSecond(1595005065053), ZoneId.systemDefault()),
                games = emptySet()
            )
        }
    }
}