package com.michibaum.usermanagement_service

import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_service.app.UpdateUserDto
import com.michibaum.usermanagement_service.app.UserService
import com.michibaum.usermanagement_service.database.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class UserServiceUT {

    private val userRepository: UserRepository = mockk()
    private val permissionRepository: PermissionRepository = mockk()
    private val userPermissionMappingRepository: UserPermissionMappingRepository = mockk()

    private val userService = UserService(
        userRepository,
        permissionRepository,
        userPermissionMappingRepository
    )

    @Test
    fun `getUserDetailsById returns details when user exists`() {
        val userId = UUID.randomUUID()
        val user = User(email = "john@example.com", id = userId)
        val perm = Permission(Permissions.CHESS_SERVICE.name)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { permissionRepository.findByUser(user) } returns listOf(perm)

        val result = userService.getUserDetailsById(userId)

        assertNotNull(result)
        assertEquals(userId.toString(), result?.id)
        assertTrue(result!!.permissions.contains(Permissions.CHESS_SERVICE.name))
    }

    @Test
    fun `getUserDetailsById returns null when user does not exist`() {
        val userId = UUID.randomUUID()
        every { userRepository.findById(userId) } returns Optional.empty()

        val result = userService.getUserDetailsById(userId)

        assertNull(result)
    }

    @Test
    fun `deleteUser deletes permission mappings and user`() {
        val userId = UUID.randomUUID()
        val user = User(email = "john@example.com", id = userId)

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { userPermissionMappingRepository.deleteByUser(user) } just Runs
        every { userRepository.delete(user) } just Runs

        userService.deleteUser(userId)

        verify(exactly = 1) { userPermissionMappingRepository.deleteByUser(user) }
        verify(exactly = 1) { userRepository.delete(user) }
    }

    @Test
    fun `deleteUser does nothing when user not found`() {
        val userId = UUID.randomUUID()

        every { userRepository.findById(userId) } returns Optional.empty()

        userService.deleteUser(userId)

        verify(exactly = 0) { userPermissionMappingRepository.deleteByUser(any()) }
        verify(exactly = 0) { userRepository.delete(any()) }
    }

    @Test
    fun `emailExists delegates to repository`() {
        every { userRepository.existsByEmail("john@example.com") } returns true
        assertTrue(userService.emailExists("john@example.com"))

        every { userRepository.existsByEmail("none@example.com") } returns false
        assertFalse(userService.emailExists("none@example.com"))
    }

    @Test
    fun `createDefaultUser saves user with email`() {
        val createUserDto = CreateUserDto("john@example.com")
        every { userRepository.save(any()) } answers { firstArg() }

        val created = userService.createDefaultUser(createUserDto)

        assertEquals("john@example.com", created.email)
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `update saves existing user when found`() {
        val userId = UUID.randomUUID()
        val existingUser = User(email = "john@example.com", id = userId)
        val updateDto = UpdateUserDto("john@example.com")

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.save(existingUser) } returns existingUser

        val updated = userService.update(userId, updateDto)

        assertNotNull(updated)
        assertEquals(userId, updated?.id)
        assertEquals("john@example.com", updated?.email)
        verify(exactly = 1) { userRepository.save(existingUser) }
    }

    @Test
    fun `update returns null when user not found`() {
        val userId = UUID.randomUUID()
        val updateDto = UpdateUserDto("john@example.com")

        every { userRepository.findById(userId) } returns Optional.empty()

        val updated = userService.update(userId, updateDto)

        assertNull(updated)
        verify(exactly = 0) { userRepository.save(any()) }
    }
}