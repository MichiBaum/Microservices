package com.michibaum.chess.domain

class PlayerProvider {

    companion object {
        fun player(): Person {
            return Person(firstname = "Michi", lastname = "Baum")
        }
    }

}