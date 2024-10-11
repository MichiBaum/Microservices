package com.michibaum.chess_service.domain

class PersonProvider {

    companion object {
        fun person(): Person {
            return Person(firstname = "Michi", lastname = "Baum", accounts = setOf())
        }
    }

}