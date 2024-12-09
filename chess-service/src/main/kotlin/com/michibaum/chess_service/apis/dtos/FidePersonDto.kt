package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.Gender
import com.michibaum.chess_service.domain.Person
import java.time.LocalDate
import java.util.UUID

class FidePersonDto(
    val firstname: String,
    val lastname: String,
    val fideId: String,
    val federation: String?,
    val gender: Gender
) {
    fun toPerson(id: UUID = UUID.randomUUID(), birthDay: LocalDate? = null, accounts: Set<Account> = emptySet()): Person {
        return Person(
            firstname = firstname,
            lastname = lastname,
            fideId = fideId,
            federation = federation,
            birthday = birthDay,
            gender = gender,
            accounts = accounts,
            id = id
        )
    }
}