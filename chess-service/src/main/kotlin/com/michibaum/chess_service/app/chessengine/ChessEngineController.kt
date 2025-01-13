package com.michibaum.chess_service.app.chessengine

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ChessEngineController(
    private val chessEngineService: ChessEngineService,
    private val chessEngineConverter: ChessEngineConverter
) {

    @GetMapping("/api/engines")
    fun getAllChessEngines(): ResponseEntity<List<ChessEngineResponseDto>> {
        val chessEngines = chessEngineService.getAllChessEngines()
        val responseDtos = chessEngineConverter.toDto(chessEngines)
        return ResponseEntity.ok(responseDtos)
    }

    @PutMapping("/api/engines")
    fun createChessEngine(@RequestBody chessEngineDto: ChessEngineDto): ResponseEntity<ChessEngineResponseDto> {
        val chessEngine = chessEngineService.createChessEngine(chessEngineDto)
        val responseDto = chessEngineConverter.toDto(chessEngine)
        return ResponseEntity.ok(responseDto)
    }

    @PostMapping("/api/engines/{id}")
    fun updateChessEngine(@PathVariable id: UUID, @RequestBody chessEngineDto: ChessEngineDto): ResponseEntity<ChessEngineResponseDto> {
        val chessEngine = chessEngineService.updateChessEngine(id, chessEngineDto)
        val responseDto = chessEngineConverter.toDto(chessEngine)
        return ResponseEntity.ok(responseDto)
    }


}