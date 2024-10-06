package com.michibaum.chess.app

import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.Person
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
) {

    fun connectAccount(person: Person, account: Account): Person {
        val newPlayer = person.copy(accounts = person.accounts + account)
        return playerRepository.save(newPlayer)
    }

    fun connectAccounts(person: Person, accounts: List<Account>): Person {
        val newPlayer = person.copy(accounts = person.accounts + accounts)
        return playerRepository.save(newPlayer)
    }

}