package com.michibaum.authentication_service.registration

import com.michibaum.authentication_service.app.registration.RegisterDto
import com.michibaum.authentication_service.app.registration.RegisterResponse
import com.michibaum.authentication_service.app.registration.RegisterState
import com.michibaum.authentication_service.app.registration.RegistrationController
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.CreateUserDto
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder

class RegistrationControllerUT {

    private val usermanagementClient: UsermanagementClient = mockk()
    private val userCredentialsRepository: UserCredentialsRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val controller = RegistrationController(
        usermanagementClient,
        userCredentialsRepository,
        passwordEncoder
    )

    @Test
    fun `Registration successful`() {
        val registerDto = RegisterDto("alice", "alice@example.com", "secret")
        val createdUser = UserDetailsDto(id = "user-uuid-123", permissions = emptySet())

        every { userCredentialsRepository.existsByUsername("alice") } returns false
        every { usermanagementClient.create(any()) } returns createdUser
        every { passwordEncoder.encode("secret") } returns "hashedSecret"
        every { userCredentialsRepository.save(any()) } answers { firstArg() }

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(RegisterState.SUCCESS, response.body?.state)
        assertEquals("alice", response.body?.username)
        verify(exactly = 1) { userCredentialsRepository.save(match { it.userId == createdUser.id && it.username == "alice" }) }
    }

    @Test
    fun `Registration conflict when username exists in credentials repository`() {
        val registerDto = RegisterDto("alice", "alice@example.com", "secret")

        every { userCredentialsRepository.existsByUsername("alice") } returns true

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(RegisterState.ERROR, response.body?.state)
        verify(exactly = 0) { usermanagementClient.create(any()) }
    }

    @Test
    fun `Registration conflict when usermanagement rejects creation`() {
        val registerDto = RegisterDto("alice", "alice@example.com", "secret")

        every { userCredentialsRepository.existsByUsername("alice") } returns false
        every { usermanagementClient.create(any()) } returns null

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(RegisterState.ERROR, response.body?.state)
        verify(exactly = 0) { userCredentialsRepository.save(any()) }
    }

    @Test
    fun `Registration triggers compensating rollback when credential save fails`() {
        val registerDto = RegisterDto("alice", "alice@example.com", "secret")
        val createdUser = UserDetailsDto(id = "user-uuid-123", permissions = emptySet())

        every { userCredentialsRepository.existsByUsername("alice") } returns false
        every { usermanagementClient.create(any()) } returns createdUser
        every { passwordEncoder.encode("secret") } returns "hashedSecret"
        every { userCredentialsRepository.save(any()) } throws RuntimeException("DB error")
        every { usermanagementClient.delete("user-uuid-123") } returns ResponseEntity.noContent().build()

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(exactly = 1) { usermanagementClient.delete("user-uuid-123") }
    }

    @Test
    fun `Registration fails validation with blank username`() {
        val registerDto = RegisterDto("", "alice@example.com", "secret")

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Registration fails validation with blank password`() {
        val registerDto = RegisterDto("alice", "alice@example.com", "")

        val response = controller.register(registerDto)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}
