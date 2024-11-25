package com.michibaum.chess_service.domain

class PersonProvider {

    companion object {
        fun person(accounts: Set<Account> = emptySet()): Person {
            return Person(firstname = "Michi", lastname = "Baum", fideId = null, accounts = accounts)
        }
    }

}