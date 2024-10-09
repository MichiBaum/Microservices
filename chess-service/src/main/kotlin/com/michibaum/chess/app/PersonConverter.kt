package com.michibaum.chess.app

import com.michibaum.chess.domain.Person
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonConverter {
    fun convert(person: PersonDto): Person {
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
            lastname = person.lastname
        )
    }
}