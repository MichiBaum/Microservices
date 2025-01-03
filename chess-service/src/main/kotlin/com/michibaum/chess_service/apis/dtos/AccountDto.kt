package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import com.michibaum.chess_service.domain.Game
import java.time.LocalDate
import java.util.*

class AccountDto(
    val id: String,
    val url: String,
    val username: String,
    val name: String, // Realname
    val platform: ChessPlatform,
    val createdAt: LocalDate? = null
) {
    fun toAccount(id: UUID? = null, games: Set<Game> = emptySet()): Account {
        return Account(id = id, platformId = this.id, username = username, platform = platform, name = name, person = null, createdAt = createdAt, games = games)
    }
}