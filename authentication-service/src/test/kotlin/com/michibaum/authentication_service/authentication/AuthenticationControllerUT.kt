package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_service.app.authentication.AuthenticationController
import com.michibaum.authentication_service.app.authentication.AuthenticationDto
import com.michibaum.authentication_service.app.authentication.AuthenticationService
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class AuthenticationControllerUT {

    private val authenticationService: AuthenticationService = mockk()
    private val usermanagementClient: UsermanagementClient = mockk()

    private val authenticationController: AuthenticationController = AuthenticationController(authenticationService)
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
        val authenticationDto = AuthenticationDto("UName", "Passwört")

        // WHEN
        val result = authenticationController.authenticate(authenticationDto)

        // THEN
        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        assertEquals(0, result.headers.size)
    }

}