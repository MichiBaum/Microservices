package com.michibaum.chess.app

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

    @GetMapping(value = ["/api/account/{accountId}/load-games"])
    fun loadGamesFor(@PathVariable("accountId") accountId: UUID){
        accountService.findById(accountId)?.let {
            gameService.loadGamesFor(it)
        }
    }

    @GetMapping(value = ["/api/account/{accountId}/games"])
    fun getGamesFor(@PathVariable("accountId") accountId: UUID): Set<GameDto>{
        val games = accountService.findById(accountId)?.games ?: emptySet()
        return games.map(gameConverter::convert).toSet()
    }

}