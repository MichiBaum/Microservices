package com.michibaum.chess_service.domain

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import java.time.LocalDate

class PersonProvider {

    companion object {
        fun person(accounts: Set<Account> = emptySet()): Person {
            return Person(
                firstname = "Michi",
                lastname = "Baum",
                accounts = accounts,
                federation = "SUI",
                birthday = LocalDate.of(2001, 3, 31),
                gender = Gender.MALE
            )
        }
    }

}