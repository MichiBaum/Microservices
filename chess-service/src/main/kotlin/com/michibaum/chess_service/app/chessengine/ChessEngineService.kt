package com.michibaum.chess_service.app.chessengine

import com.michibaum.chess_service.database.ChessEngine
import com.michibaum.chess_service.database.ChessEngineRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ChessEngineService(
    private val chessEngineRepository: ChessEngineRepository
) {

    fun getAllChessEngines(): List<ChessEngine> =
        chessEngineRepository.findAll()

    fun createChessEngine(chessEngineDto: ChessEngineDto): ChessEngine {
        val chessEngine = ChessEngine(
            name = chessEngineDto.name,
            version = chessEngineDto.version
        )
        return chessEngineRepository.save(chessEngine)
    }

    fun updateChessEngine(id: UUID, chessEngineDto: ChessEngineDto): ChessEngine {
        val existingChessEngine = chessEngineRepository.findById(id).orElseThrow {
            IllegalArgumentException("ChessEngine with id $id not found")
        }

        val updatedEntity = existingChessEngine.copy(
            name = chessEngineDto.name,
            version = chessEngineDto.version
        )

        return chessEngineRepository.save(updatedEntity)
    }

    fun findById(id: UUID): ChessEngine? {
        return chessEngineRepository.findById(id).getOrNull()
    }

}