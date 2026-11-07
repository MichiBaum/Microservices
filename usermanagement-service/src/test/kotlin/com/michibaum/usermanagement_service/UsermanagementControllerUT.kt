package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_service.app.UserService
import com.michibaum.usermanagement_service.app.UsermanagementController
import com.michibaum.usermanagement_service.database.PermissionRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class UsermanagementControllerUT {

    private val userService: UserService = mockk()
    private val permissionRepository: PermissionRepository = mockk()

    private val controller = UsermanagementController(userService, permissionRepository)

    @Test
    fun `getUserDetailsById returns details when found`() {
        val userId = UUID.randomUUID()
        val userDetails = UserDetailsDto(
            id = userId.toString(),
            username = "alice",
            permissions = setOf("CHESS_SERVICE")
        )
        every { userService.getUserDetailsById(userId) } returns userDetails

        val result = controller.getUserDetailsById(userId.toString())

        assertEquals(userDetails, result)
    }

    @Test
    fun `getUserDetailsById returns null when not found`() {
        val userId = UUID.randomUUID()
        every { userService.getUserDetailsById(userId) } returns null

        val result = controller.getUserDetailsById(userId.toString())

        assertNull(result)
    }

    @Test
    fun `getUserDetailsById returns null on invalid UUID`() {
        val result = controller.getUserDetailsById("not-a-uuid")

        assertNull(result)
        verify(exactly = 0) { userService.getUserDetailsById(any()) }
    }

    @Test
    fun `delete returns no content on valid UUID`() {
        val userId = UUID.randomUUID()
        every { userService.deleteUser(userId) } just Runs

        val response = controller.delete(userId.toString())

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(exactly = 1) { userService.deleteUser(userId) }
    }

    @Test
    fun `delete returns bad request on invalid UUID`() {
        val response = controller.delete("not-a-uuid")

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        verify(exactly = 0) { userService.deleteUser(any()) }
    }
}
