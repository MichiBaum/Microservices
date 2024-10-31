package com.michibaum.fitness_service

import org.junit.jupiter.api.Assertions

fun assertNotEmpty(input: String){
    Assertions.assertNotNull(input)
    Assertions.assertNotEquals("", input.trim())
}