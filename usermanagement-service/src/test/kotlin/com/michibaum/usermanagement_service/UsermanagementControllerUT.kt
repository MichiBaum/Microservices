package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_service.app.UserService
import com.michibaum.usermanagement_service.app.UsermanagementController
import com.michibaum.usermanagement_service.database.Permission
import com.michibaum.usermanagement_service.database.PermissionRepository
import com.michibaum.usermanagement_service.database.User
import com.michibaum.usermanagement_service.database.UserPermissionMapping
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

    @Test
    fun `create returns user details when input is valid and user does not exist`() {
        val createDto = CreateUserDto("alice@example.com")
        val userId = UUID.randomUUID()
        val user = User(email = "alice@example.com", id = userId)
        val perm = Permission("USERMANAGEMENT_SERVICE_EDIT_OWN_USER")
        val mapping = UserPermissionMapping(user, perm)

        every { userService.emailExists("alice@example.com") } returns false
        every { userService.createDefaultUser(createDto) } returns user
        every { userService.addDefaultPermissions(user) } returns listOf(mapping)

        val result = controller.create(createDto)

        assertEquals(userId.toString(), result?.id)
        assertNull(result?.username)
        assertEquals(setOf("USERMANAGEMENT_SERVICE_EDIT_OWN_USER"), result?.permissions)
    }

    @Test
    fun `create returns null when email already exists`() {
        val createDto = CreateUserDto("alice@example.com")
        every { userService.emailExists("alice@example.com") } returns true

        val result = controller.create(createDto)

        assertNull(result)
        verify(exactly = 0) { userService.createDefaultUser(any()) }
    }

    @Test
    fun `create returns null when validation fails`() {
        val createDto = CreateUserDto("")

        val result = controller.create(createDto)

        assertNull(result)
        verify(exactly = 0) { userService.emailExists(any()) }
    }
}
