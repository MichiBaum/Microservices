package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.app.account.AccountService
import com.michibaum.chess_service.database.Game
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GameController(
    private val gameService: GameService,
    private val accountService: AccountService,
    private val gameConverter: GameConverter
) {

    @GetMapping(value = ["/api/accounts/{id}/load-games"])
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun loadGamesFor(@PathVariable id: String): ResponseEntity<Nothing>{
        return try {
            val uuid = UUID.fromString(id)
            accountService.findByAccountId(uuid)?.let {
                gameService.loadGamesFor(it)
            }
            ResponseEntity.ok().build()
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping(value = ["/api/accounts/{id}/games"])
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun getGamesFor(@PathVariable id: String): ResponseEntity<Set<GameDto>>{
        return try {
            val uuid = UUID.fromString(id)
            val games = emptySet<Game>() // TODO get all player for user and therefore all games
            val dtos = games.map(gameConverter::convert).toSet()
            ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}