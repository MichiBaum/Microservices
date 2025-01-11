package com.michibaum.chess_service.app.chessengine

import com.michibaum.chess_service.domain.ChessEngine
import org.springframework.stereotype.Component

@Component
class ChessEngineConverter {

    fun toDto(chessEngine: ChessEngine): ChessEngineResponseDto {
        return ChessEngineResponseDto(
            id = chessEngine.id?.toString() ?: "",
            name = chessEngine.name,
            version = chessEngine.version
        )
    }

    fun toDto(chessEngines: List<ChessEngine>): List<ChessEngineResponseDto> =
        chessEngines.map { toDto(it)}

}