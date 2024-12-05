package com.michibaum.chess_service.app.person

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
            fideId = person.fideId,
            accounts = person.accounts + account,
            id = person.id,
            federation = person.federation,
            birthDate = person.birthDate,
            gender = person.gender
        )
        return personRepository.save(newPerson)
    }

    fun connectAccounts(person: Person, accounts: List<Account>): Person {
        val newPerson = Person(
            firstname = person.firstname,
            lastname = person.lastname,
            fideId = person.fideId,
            accounts = person.accounts + accounts,
            id = person.id,
            federation = person.federation,
            birthDate = person.birthDate,
            gender = person.gender
        )
        return personRepository.save(newPerson)
    }

    fun savePerson(person: Person): Person {
        return personRepository.save(person)
    }

    fun findByIfNotEmpty(firstname: String, lastname: String): Set<Person> =
        when{
            firstname.isBlank() && lastname.isBlank() -> emptySet()
            firstname.isBlank() -> personRepository.findByLastnameContainingIgnoreCase(lastname)
            lastname.isBlank() -> personRepository.findByFirstnameContainingIgnoreCase(firstname)
            else -> personRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname)
        }

    fun getAll(): List<Person> {
        return personRepository.findAll()
    }

}