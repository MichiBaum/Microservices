package com.michibaum.usermanagement_library

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class UserDetailsDtoUT {

    @Test
    fun `test user details creation and properties`() {
        val permissions = setOf("READ", "WRITE")
        val dto = UserDetailsDto(id = "user-123", permissions = permissions)

        assertEquals("user-123", dto.id)
        assertEquals(permissions, dto.permissions)
    }

    @Test
    fun `test user details equality`() {
        val dto1 = UserDetailsDto(id = "1", permissions = setOf("ADMIN"))
        val dto2 = UserDetailsDto(id = "1", permissions = setOf("ADMIN"))
        val dto3 = UserDetailsDto(id = "2", permissions = setOf("ADMIN"))

        assertEquals(dto1, dto2)
        assertNotEquals(dto1, dto3)
    }

}
