package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.app.account.AccountConverter
import com.michibaum.chess_service.domain.Person
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PersonConverter(
    private val accountConverter: AccountConverter
) {
    fun convert(person: CreatePersonDto): Person {
        return Person(
            firstname = person.firstname,
            lastname = person.lastname,
            fideId = person.fideId,
            accounts = emptySet(),
            federation = person.federation,
            birthday = person.birthday?.let { LocalDate.parse(it) },
            gender = person.gender
        )
    }

    fun convert(person: Person): PersonDto {
        return PersonDto(
            id = person.idOrThrow(),
            firstname = person.firstname,
            lastname = person.lastname,
            fideId = person.fideId,
            federation = person.federation,
            birthday = person.birthday?.toString(),
            gender = person.gender,
            accounts = person.accounts.map { account -> accountConverter.convert(account) }.toSet(),
        )
    }
}