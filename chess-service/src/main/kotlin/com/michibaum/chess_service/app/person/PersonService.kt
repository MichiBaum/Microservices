package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PersonService(
    private val personRepository: PersonRepository,
) {

    fun connectAccount(person: Person, account: Account): Person {
        val newPerson = Person(
            firstname = person.firstname,
            lastname = person.lastname,
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
            accounts = person.accounts + accounts,
            id = person.id,
            federation = person.federation,
            birthday = person.birthday,
            gender = person.gender
        )
        return personRepository.save(newPerson)
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

    fun find(uuid: UUID): Person? =
        personRepository.findById(uuid).getOrNull()

    fun update(person: Person, personDto: WritePersonDto): Person {
        val newPerson = Person(
            firstname = personDto.firstname,
            lastname = personDto.lastname,
            federation = personDto.federation,
            birthday = personDto.birthday?.let { LocalDate.parse(it) },
            gender = personDto.gender,
            accounts = person.accounts,
            id = person.id
        )
        return personRepository.save(newPerson)
    }

    fun create(personDto: WritePersonDto): Person{
        val newPerson = Person(
            firstname = personDto.firstname,
            lastname = personDto.lastname,
            federation = personDto.federation,
            birthday = personDto.birthday?.let { LocalDate.parse(it) },
            gender = personDto.gender,
            accounts = emptySet(),
        )
        return personRepository.save(newPerson)
    }

}