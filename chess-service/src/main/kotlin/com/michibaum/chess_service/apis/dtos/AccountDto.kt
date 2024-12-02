package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import java.time.LocalDate
import java.util.*

class AccountDto(
    val id: String,
    val url: String,
    val username: String,
    val name: String, // Realname
    val platform: ChessPlatform,
    val createdAt: LocalDate
) {
    fun toAccount(): Account {
        return Account(platformId = id, username = username, platform = platform, url = url, name = name, person = null, createdAt = createdAt, games = emptySet(), id = UUID.randomUUID())
    }
}