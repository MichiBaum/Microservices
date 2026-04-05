package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.app.account.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GameController(
    private val gameService: GameService,
    private val accountService: AccountService,
    private val gameConverter: GameConverter
) {

    @GetMapping(value = ["/api/games/{id}"])
    fun getGame(@PathVariable id: String): ResponseEntity<GameResponseDto> {
        return try {
            val uuid = UUID.fromString(id)

            val game = gameService.findById(uuid) ?:
                return ResponseEntity.notFound().build()

            ResponseEntity.ok(gameConverter.convert(game))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping(value = ["/api/games"])
    fun create(@RequestBody gameRequestDto: GameRequestDto): ResponseEntity<GameResponseDto> {
        return try {
            val game = gameService.create(gameRequestDto)
            ResponseEntity.ok(gameConverter.convert(game))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PutMapping(value = ["/api/games/{id}"])
    fun update(@PathVariable id: String, @RequestBody gameRequestDto: GameRequestDto): ResponseEntity<GameResponseDto> {
        return try {
            val uuid = UUID.fromString(id)
            val game = gameService.update(uuid, gameRequestDto) ?: return ResponseEntity.notFound().build()
            ResponseEntity.ok(gameConverter.convert(game))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

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
    fun getGamesFor(@PathVariable id: String): ResponseEntity<List<GameResponseDto>>{
        return try {
            val uuid = UUID.fromString(id)
            val games = gameService.getByAccount(uuid)
            val dtos = games.map(gameConverter::convert)
            ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping(value = ["/api/games/by-accounts"])
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun getGamesBetween(@RequestParam("ids") ids: List<String>): ResponseEntity<List<GameResponseDto>>{
        return try {
            if (ids.size != 2) return ResponseEntity.badRequest().build()
            val uuid1 = UUID.fromString(ids[0])
            val uuid2 = UUID.fromString(ids[1])
            val games = gameService.getByAccounts(uuid1, uuid2)
            val dtos = games.map(gameConverter::convert)
            ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping(value = ["/api/games/{id}"])
    fun delete(@PathVariable id: String): ResponseEntity<Nothing> {
        return try {
            val uuid = UUID.fromString(id)
            gameService.delete(uuid)
            ResponseEntity.ok().build()
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}