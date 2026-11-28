package com.michibaum.authentication_service.basic_authentication

import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDto
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDtoValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthenticationDtoValidatorUT {

    @Test
    fun `validate returns empty list for valid dto`() {
        val dto = AuthenticationDto("alice", "password123")
        val errors = AuthenticationDtoValidator.validate(dto)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validate returns username error when username is blank`() {
        val dto = AuthenticationDto("   ", "password123")
        val errors = AuthenticationDtoValidator.validate(dto)
        assertEquals(listOf("username"), errors)
    }

    @Test
    fun `validate returns password error when password is blank`() {
        val dto = AuthenticationDto("alice", "   ")
        val errors = AuthenticationDtoValidator.validate(dto)
        assertEquals(listOf("password"), errors)
    }

    @Test
    fun `validate returns both errors when both fields are blank`() {
        val dto = AuthenticationDto("", "")
        val errors = AuthenticationDtoValidator.validate(dto)
        assertEquals(listOf("username", "password"), errors)
    }
}
