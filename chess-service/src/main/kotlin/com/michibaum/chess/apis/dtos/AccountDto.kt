package com.michibaum.chess.apis.dtos

import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.ChessPlatform
import java.util.*

class AccountDto(
    val id: String,
    val url: String,
    val username: String,
    val name: String, // Realname
    val platform: ChessPlatform,
    val createdAt: Date
) {
    fun toAccount(): Account {
        return Account(accId = id, username = username, platform = platform, url = url, name = name, person = null, createdAt = createdAt)
    }
}