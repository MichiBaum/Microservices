package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonConverter(
    private val accountConverter: AccountConverter
) {
    fun convert(person: CreatePersonDto): Person {
        return Person(
            id = UUID.randomUUID(),
            firstname = person.firstname,
            lastname = person.lastname,
            accounts = emptySet()
        )
    }

    fun convert(person: Person): PersonDto {
        return PersonDto(
            id = person.id,
            firstname = person.firstname,
            lastname = person.lastname,
            accounts = person.accounts.map { account -> accountConverter.convert(account) }.toSet()
        )
    }
}