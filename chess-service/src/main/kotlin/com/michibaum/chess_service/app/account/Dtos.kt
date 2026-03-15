package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.ChessPlatform
import java.util.*

data class GetAccountDto(
    val id: UUID?,
    val platformId: String,
    val name: String,
    val username: String,
    val platform: ChessPlatform,
    val url: String,
    val person: GetAccountPersonDto?,
    val createdAt: String?,
)

data class GetAccountPersonDto(
    val id: UUID,
    val name: String,
)

enum class SearchLocation {
    LOCAL,
    ONLINE,
}