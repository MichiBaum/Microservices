package com.michibaum.usermanagement_library

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreateUserDtoValidatorUT {

    @Test
    fun `test valid CreateUserDto produces no errors`() {
        val dto = CreateUserDto(email = "test@example.com")
        val errors = CreateUserDtoValidator.validate(dto)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `test blank email produces error`() {
        val dto = CreateUserDto(email = "   ")
        val errors = CreateUserDtoValidator.validate(dto)

        assertEquals(1, errors.size)
        assertEquals("email", errors[0])
    }

    @Test
    fun `test empty email produces error`() {
        val dto = CreateUserDto(email = "")
        val errors = CreateUserDtoValidator.validate(dto)

        assertEquals(1, errors.size)
        assertEquals("email", errors[0])
    }

}
