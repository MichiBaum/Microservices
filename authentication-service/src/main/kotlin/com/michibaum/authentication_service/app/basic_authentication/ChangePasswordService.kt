package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_service.database.UserCredentialsRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangePasswordService(
    private val userCredentialsRepository: UserCredentialsRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun changePassword(userId: String, dto: ChangePasswordDto) {
        val credentials = userCredentialsRepository.findByUserId(userId)
            ?: throw UserCredentialsNotFoundException("User credentials not found")

        if (!credentials.enabled || !passwordEncoder.matches(dto.currentPassword, credentials.passwordHash)) {
            throw InvalidCurrentPasswordException("Current password does not match")
        }

        if (dto.currentPassword == dto.newPassword || passwordEncoder.matches(dto.newPassword, credentials.passwordHash)) {
            throw SamePasswordException("New password must be different from current password")
        }

        credentials.passwordHash = requireNotNull(passwordEncoder.encode(dto.newPassword)) { "Encoded password must not be null" }
        userCredentialsRepository.save(credentials)
    }
}
