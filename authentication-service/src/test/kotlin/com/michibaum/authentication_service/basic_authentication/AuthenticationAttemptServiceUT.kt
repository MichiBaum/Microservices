package com.michibaum.authentication_service.basic_authentication

import com.michibaum.authentication_service.app.basic_authentication.AuthenticationAttemptService
import com.michibaum.authentication_service.database.AuthenticationAttempt
import com.michibaum.authentication_service.database.AuthenticationAttemptRepository
import com.michibaum.authentication_service.database.JwtEntity
import com.michibaum.authentication_service.database.JwtRepository
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationRepository
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthenticationAttemptServiceUT {

    private val basicAuthenticationRepository: BasicAuthenticationRepository = mockk()
    private val authenticationAttemptRepository: AuthenticationAttemptRepository = mockk()
    private val jwtRepository: JwtRepository = mockk()

    private val authenticationAttemptService = AuthenticationAttemptService(
        basicAuthenticationRepository,
        authenticationAttemptRepository,
        jwtRepository
    )

    @Test
    fun `Create attempt`() {
        val username = "user"
        val authAttempt = AuthenticationAttempt()
        val basicAuthAttempt = BasicAuthenticationAttempt(authAttempt, username)

        every { authenticationAttemptRepository.save(any()) } returns authAttempt
        every { basicAuthenticationRepository.save(any()) } returns basicAuthAttempt

        val result = authenticationAttemptService.createAttempt(username)

        assertEquals(username, result.username)
        verify { authenticationAttemptRepository.save(any()) }
        verify { basicAuthenticationRepository.save(any()) }
    }

    @Test
    fun `Attempt failed`() {
        val authAttempt = AuthenticationAttempt()
        val basicAuthAttempt = BasicAuthenticationAttempt(authAttempt, "user")
        val basicAuthFailure = BasicAuthenticationFailure(basicAuthAttempt, "user")

        every { basicAuthenticationRepository.delete(any()) } returns Unit
        every { basicAuthenticationRepository.save(any()) } returns basicAuthFailure

        val result = authenticationAttemptService.attemptFailed(basicAuthAttempt)

        assertEquals(basicAuthFailure, result)
        verify { basicAuthenticationRepository.delete(basicAuthAttempt) }
        verify { basicAuthenticationRepository.save(any()) }
    }

    @Test
    fun `Attempt successful`() {
        val authAttempt = AuthenticationAttempt()
        val basicAuthAttempt = BasicAuthenticationAttempt(authAttempt, "user")
        val jwtEntity = JwtEntity(jwt = "jws")
        val basicAuthSuccess = BasicAuthenticationSuccess("userId", jwtEntity, basicAuthAttempt, "user")

        every { jwtRepository.save(any()) } returns jwtEntity
        every { basicAuthenticationRepository.delete(any()) } returns Unit
        every { basicAuthenticationRepository.save(any()) } returns basicAuthSuccess

        val result = authenticationAttemptService.attemptSuccessful(basicAuthAttempt, "userId", "jws")

        assertEquals(basicAuthSuccess, result)
        verify { jwtRepository.save(any()) }
        verify { basicAuthenticationRepository.delete(basicAuthAttempt) }
        verify { basicAuthenticationRepository.save(any()) }
    }
}
