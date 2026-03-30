package com.michibaum.chess_service.app.game_move

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class GameMoveController(
    
) {
    
    @GetMapping("/game/{id}/game-moves")
    fun getGameMoves(@PathVariable id: String): ResponseEntity<Set<>> {
        return try {
            val uuid = UUID.fromString(id)

        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}