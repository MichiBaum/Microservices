package com.michibaum.chess.app

import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.Person
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personRepository: PersonRepository,
) {

    fun connectAccount(person: Person, account: Account): Person {
        val newPlayer = person.copy(accounts = person.accounts + account)
        return personRepository.save(newPlayer)
    }

    fun connectAccounts(person: Person, accounts: List<Account>): Person {
        val newPlayer = person.copy(accounts = person.accounts + accounts)
        return personRepository.save(newPlayer)
    }

    fun savePerson(person: Person): Person {
        return personRepository.save(person)
    }

}