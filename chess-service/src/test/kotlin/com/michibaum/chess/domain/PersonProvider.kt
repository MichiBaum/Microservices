package com.michibaum.chess.domain

class PersonProvider {

    companion object {
        fun person(): Person {
            return Person(firstname = "Michi", lastname = "Baum")
        }
    }

}