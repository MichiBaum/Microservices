package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.ChessPlatform
import java.util.*

data class AccountDto(
    val id: UUID,
    val name: String,
    val username: String,
    val platform: ChessPlatform,
    val url: String,
    val personName: String?,
)
