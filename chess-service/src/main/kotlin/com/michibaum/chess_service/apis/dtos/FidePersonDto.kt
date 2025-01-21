package com.michibaum.chess_service.apis.dtos

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import java.time.LocalDate
import java.util.*

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
            federation = federation,
            birthday = birthDay,
            gender = gender,
            accounts = accounts,
            id = id
        )
    }
}