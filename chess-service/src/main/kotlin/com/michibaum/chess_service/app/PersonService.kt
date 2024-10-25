package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personRepository: PersonRepository,
) {

    fun connectAccount(person: Person, account: Account): Person {
        val newPerson = Person(
            firstname = person.firstname,
            lastname = person.lastname,
            accounts = person.accounts + account,
            id = person.id
        )
        return personRepository.save(newPerson)
    }

    fun connectAccounts(person: Person, accounts: List<Account>): Person {
        val newPerson = Person(
            firstname = person.firstname,
            lastname = person.lastname,
            accounts = person.accounts + accounts,
            id = person.id
        )
        return personRepository.save(newPerson)
    }

    fun savePerson(person: Person): Person {
        return personRepository.save(person)
    }

    fun findPersonsByFirstnameAndLastname(firstname: String, lastname: String): Set<Person> {
        return personRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname)
    }

}