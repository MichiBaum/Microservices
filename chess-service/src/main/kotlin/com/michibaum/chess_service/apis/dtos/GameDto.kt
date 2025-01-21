package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.GameType


data class GameDto(
    val chessPlatform: ChessPlatform,
    val id: String,
    val players: List<PlayerDto>,
    val pgn: String,
    val gameType: GameType
)

data class PlayerDto(
    val id: String,
    val username: String,
    val rating: Long,
    val pieceColor: PieceColor
)

enum class PieceColor{
    WHITE,
    BLACK
}