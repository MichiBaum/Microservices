package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.ChessPlatform
import java.util.UUID

data class AccountDto(
    val id: UUID,
    val username: String,
    val platform: ChessPlatform,
    val url: String,
)
