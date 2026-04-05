package com.michibaum.chess_service.app.game_move

import com.michibaum.chess_service.app.game.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class GameMoveController(
    private val gameMoveService: GameMoveService,
    private val gameService: GameService,
    private val gameMoveConverter: GameMoveConverter
) {

    @GetMapping("/api/games/{id}/moves")
    fun getGameMoves(@PathVariable id: String): ResponseEntity<List<GameMoveResponseDto>> {
        return try {
            val uuid = UUID.fromString(id)
            val game = gameService.findById(uuid) ?: return ResponseEntity.notFound().build()
            val moves = gameMoveService.findByGame(game)
            ResponseEntity.ok(moves.map(gameMoveConverter::convert))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}