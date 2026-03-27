package com.michibaum.authentication_service.basic_authentication

import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationController
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDto
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationResponse
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationAttemptService
import com.michibaum.authentication_service.database.AuthenticationAttempt
import com.michibaum.authentication_service.database.JwtEntity
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationSuccess
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthenticationControllerUT {

    private val authenticationService: AuthenticationService = mockk()
    private val authenticationAttemptService: AuthenticationAttemptService = mockk()
    private val usermanagementClient: UsermanagementClient = mockk()

    private val authenticationController: AuthenticationController = AuthenticationController(authenticationService, usermanagementClient, authenticationAttemptService)

    @Test
    fun `Authentication successful`(){
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "ubdf-sdfg-sdfv-sdfv",
            username = "UName",
            permissions = emptySet()
        )
        every { usermanagementClient.checkUserDetails(any()) } returns userDetailsDto
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptSuccessful(any(), any(), any()) } answers { BasicAuthenticationSuccess(userDetailsDto.id, JwtEntity(jwt = thirdArg()), firstArg(), secondArg()) }

        val jws = "1234qwer"
        val authenticationDto = AuthenticationDto("UName", "Passwört")
        every { authenticationService.generateJWS(userDetailsDto) } returns jws

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(jws, result.body?.jwt ?: "")
        assertEquals(authenticationDto.username, result.body?.username ?: "")
    }

    @Test
    fun `Bad credentials`(){
        // GIVEN
        every { usermanagementClient.checkUserDetails(any()) } returns null
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(AuthenticationAttempt(), firstArg()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(firstArg(), firstArg<BasicAuthenticationAttempt>().username) }
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        assertEquals(0, result.headers.size())
    }

    @Test
    fun `Validation fails with empty username`(){
        // GIVEN
        val authenticationDto = AuthenticationDto("", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Validation fails with empty password`(){
        // GIVEN
        val authenticationDto = AuthenticationDto("UName", "")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Public key returns correctly`(){
        // GIVEN
        val publicKeyDto = PublicKeyDto("RSA", "key".toByteArray())
        every { authenticationService.publicKey } returns publicKeyDto

        // WHEN
        val result = authenticationController.publicKey()

        // THEN
        assertArrayEquals(publicKeyDto.key, result.key)
        assertEquals(publicKeyDto.algorithm, result.algorithm)
    }

}