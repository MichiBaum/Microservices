package com.michibaum.authentication_service.basic_authentication

import com.michibaum.authentication_service.app.basic_authentication.AuthenticationController
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDto
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationAttemptService
import com.michibaum.authentication_service.database.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.BasicAuthenticationSuccess
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import io.mockk.every
import io.mockk.mockk
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

    private val authenticationController: AuthenticationController = AuthenticationController(authenticationService, authenticationAttemptService)
    init {
        authenticationController.usermanagementClient = usermanagementClient
    }

    @Test
    fun `Authentication successful`(){
        // GIVEN
        val userDetailsDto = UserDetailsDto(
            id = "ubdf-sdfg-sdfv-sdfv",
            username = "UName",
            permissions = emptySet()
        )
        every { usermanagementClient.checkUserDetails(any()) } returns userDetailsDto
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(firstArg(), UUID.randomUUID()) }
        every { authenticationAttemptService.attemptSuccessful(any(), any(), any()) } answers {BasicAuthenticationSuccess("", "", "", UUID.randomUUID())}

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
        every { authenticationAttemptService.createAttempt(any()) } answers { BasicAuthenticationAttempt(firstArg(), UUID.randomUUID()) }
        every { authenticationAttemptService.attemptFailed(any()) } answers { BasicAuthenticationFailure(UUID.randomUUID(), "")}
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        assertEquals(0, result.headers.size)
    }

}