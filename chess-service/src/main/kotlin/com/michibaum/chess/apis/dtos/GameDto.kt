package com.michibaum.chess.apis.dtos

import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.GameType


data class GameDto(
    val chessPlatform: ChessPlatform,
    val id: String?,
    val players: List<Player>,
    val pgn: String,
    val gameType: GameType
)

data class Player(
    val id: String,
    val username: String,
    val rating: Long,
    val pieceColor: PieceColor
)

enum class PieceColor{
    WHITE,
    BLACK
}