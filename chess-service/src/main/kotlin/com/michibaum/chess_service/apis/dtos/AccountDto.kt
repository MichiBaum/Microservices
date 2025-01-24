package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
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
    fun toAccount(id: UUID? = null): Account {
        return Account(id = id, platformId = this.id, username = username, platform = platform, name = name, person = null, createdAt = createdAt)
    }
}