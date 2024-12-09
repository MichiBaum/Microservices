package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.apis.dtos.FidePersonDto
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
            birthday = person.birthday,
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
            birthday = person.birthday,
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

    fun createAndUpdate(persons: List<FidePersonDto>): List<Person> {
        return persons.map {
            val foundPerson = personRepository.findByFideId(it.fideId)
            if (foundPerson != null) {
                val updatedPerson = it.toPerson(id = foundPerson.id, birthDay = foundPerson.birthday, accounts = foundPerson.accounts)
                personRepository.save(updatedPerson)
            } else {
                personRepository.save(it.toPerson())
            }
        }.toList()
    }

}