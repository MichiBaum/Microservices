package com.michibaum.usermanagement_service

import com.michibaum.permission_library.Permissions
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_service.app.UpdateUserDto
import com.michibaum.usermanagement_service.app.UserService
import com.michibaum.usermanagement_service.database.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceUT {

    private val userRepository: UserRepository = mockk()
    private val permissionRepository: PermissionRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val userPermissionMappingRepository: UserPermissionMappingRepository = mockk()

    private val userService = UserService(
        userRepository,
        permissionRepository,
        passwordEncoder,
        userPermissionMappingRepository
    )

    @Test
    fun `getUserDetailsByUsername returns details when user exists`() {
        val userId = UUID.randomUUID()
        val user = User("john_doe", "john@example.com", "hash", userId)
        val perm = Permission(Permissions.CHESS_SERVICE.name)

        every { userRepository.findByUsername("john_doe") } returns user
        every { permissionRepository.findByUser(user) } returns listOf(perm)

        val result = userService.getUserDetailsByUsername("john_doe")

        assertNotNull(result)
        assertEquals(userId.toString(), result?.id)
        assertEquals("john_doe", result?.username)
        assertTrue(result!!.permissions.contains(Permissions.CHESS_SERVICE.name))
    }

    @Test
    fun `getUserDetailsByUsername returns null when user does not exist`() {
        every { userRepository.findByUsername("unknown") } returns null

        val result = userService.getUserDetailsByUsername("unknown")

        assertNull(result)
    }

    @Test
    fun `deleteUser deletes permission mappings and user`() {
        val userId = UUID.randomUUID()
        val user = User("john_doe", "john@example.com", "hash", userId)

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
    fun `createDefaultUser hashes password and saves user`() {
        val createUserDto = CreateUserDto("john_doe", "john@example.com", "rawPassword")
        every { passwordEncoder.encode("rawPassword") } returns "encodedPassword"
        every { userRepository.save(any()) } answers { firstArg() }

        val created = userService.createDefaultUser(createUserDto)

        assertEquals("john_doe", created.username)
        assertEquals("john@example.com", created.email)
        assertEquals("encodedPassword", created.password)
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `update saves user when found`() {
        val userId = UUID.randomUUID()
        val existingUser = User("john_doe", "john@example.com", "hash", userId)
        val updateDto = UpdateUserDto("john_updated", "john_updated@example.com")

        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { userRepository.save(existingUser) } returns existingUser

        val updated = userService.update(userId, updateDto)

        assertNotNull(updated)
        verify(exactly = 1) { userRepository.save(existingUser) }
    }
}