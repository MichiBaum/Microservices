package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.ChessPlatform
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

class WriteAccountDto(
    @field:NotEmpty
    val platformId: String,

    @field:NotEmpty
    val name: String,

    @field:NotEmpty
    val username: String,

    @field:NotNull
    val platform: ChessPlatform,

    @field:Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\$")
    val createdAt: String? = null,

    val personId: String? = null
)
