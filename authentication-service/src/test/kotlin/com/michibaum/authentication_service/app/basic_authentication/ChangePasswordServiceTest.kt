package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class ChangePasswordServiceTest {

    private val userCredentialsRepository: UserCredentialsRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private lateinit var changePasswordService: ChangePasswordService

    private val userId = "11111111-2222-3333-4444-555555555555"

    @BeforeEach
    fun setUp() {
        changePasswordService = ChangePasswordService(userCredentialsRepository, passwordEncoder)
    }

    @Test
    fun `changePassword updates password hash and saves credentials on happy path`() {
        val oldHash = "encodedOldPassword"
        val newHash = "encodedNewPassword"
        val credentials = UserCredentials(
            userId = userId,
            username = "testuser",
            passwordHash = oldHash,
            enabled = true
        )

        every { userCredentialsRepository.findByUserId(userId) } returns credentials
        every { passwordEncoder.matches("OldPassword123!", oldHash) } returns true
        every { passwordEncoder.matches("NewSecurePassword456!", oldHash) } returns false
        every { passwordEncoder.encode("NewSecurePassword456!") } returns newHash
        every { userCredentialsRepository.save(credentials) } returns credentials

        val dto = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        changePasswordService.changePassword(userId, dto)

        assertEquals(newHash, credentials.passwordHash)
        verify(exactly = 1) { userCredentialsRepository.save(credentials) }
    }

    @Test
    fun `changePassword throws UserCredentialsNotFoundException when credentials do not exist`() {
        every { userCredentialsRepository.findByUserId(userId) } returns null

        val dto = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        assertThrows(UserCredentialsNotFoundException::class.java) {
            changePasswordService.changePassword(userId, dto)
        }

        verify(exactly = 0) { userCredentialsRepository.save(any()) }
        verify(exactly = 0) { passwordEncoder.matches(any(), any()) }
    }

    @Test
    fun `changePassword throws AccountDisabledException when account is disabled`() {
        val credentials = UserCredentials(
            userId = userId,
            username = "testuser",
            passwordHash = "encodedOldPassword",
            enabled = false
        )

        every { userCredentialsRepository.findByUserId(userId) } returns credentials

        val dto = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        assertThrows(AccountDisabledException::class.java) {
            changePasswordService.changePassword(userId, dto)
        }

        verify(exactly = 0) { userCredentialsRepository.save(any()) }
        verify(exactly = 0) { passwordEncoder.matches(any(), any()) }
    }

    @Test
    fun `changePassword throws InvalidCurrentPasswordException when current password does not match`() {
        val credentials = UserCredentials(
            userId = userId,
            username = "testuser",
            passwordHash = "encodedOldPassword",
            enabled = true
        )

        every { userCredentialsRepository.findByUserId(userId) } returns credentials
        every { passwordEncoder.matches("WrongPassword123!", "encodedOldPassword") } returns false

        val dto = ChangePasswordDto(
            currentPassword = "WrongPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        assertThrows(InvalidCurrentPasswordException::class.java) {
            changePasswordService.changePassword(userId, dto)
        }

        verify(exactly = 0) { userCredentialsRepository.save(any()) }
    }

    @Test
    fun `changePassword throws SamePasswordException when new password matches current password string`() {
        val credentials = UserCredentials(
            userId = userId,
            username = "testuser",
            passwordHash = "encodedOldPassword",
            enabled = true
        )

        every { userCredentialsRepository.findByUserId(userId) } returns credentials
        every { passwordEncoder.matches("OldPassword123!", "encodedOldPassword") } returns true

        val dto = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "OldPassword123!"
        )

        assertThrows(SamePasswordException::class.java) {
            changePasswordService.changePassword(userId, dto)
        }

        verify(exactly = 0) { userCredentialsRepository.save(any()) }
    }
}
