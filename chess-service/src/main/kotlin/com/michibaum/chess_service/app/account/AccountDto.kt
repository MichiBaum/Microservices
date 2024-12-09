package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.ChessPlatform

data class AccountDto(
    val id: String,
    val username: String,
    val platform: ChessPlatform,
    val url: String,
)
