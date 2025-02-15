package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_library.AuthenticationEndpoints
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.usermanagement_library.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController (
    private val authenticationService: AuthenticationService
) : AuthenticationEndpoints {

    @Autowired
    @Lazy
    lateinit var usermanagementClient: UsermanagementClient

    @PublicEndpoint
    @PostMapping(value = ["/api/authenticate"])
    fun authenticate(@RequestBody authenticationDto: AuthenticationDto): ResponseEntity<AuthenticationResponse> {
        val loginDto = LoginDto(authenticationDto.username, authenticationDto.password)
        val errors = LoginDtoValidator.validate(loginDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val userDetailsDto: UserDetailsDto = usermanagementClient.checkUserDetails(loginDto)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jws = authenticationService.generateJWS(userDetailsDto)!!

        // TODO Gateway logs following: 11:57:50.198 [tomcat-handler-94] WARN  o.a.h.c.h.p.ResponseProcessCookies - ex-0000000060 Cookie rejected [Authentication="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJ...", domain:michibaum.ch, path:/, expiry:null] Illegal 'domain' attribute "michibaum.ch". Domain of origin: "192.168.0.48"
        val cookie: HttpCookie = ResponseCookie.from("Authentication", jws)
            .domain(".michibaum.ch")
            .path("/")
            .build()

        val responseBody = AuthenticationResponse(authenticationDto.username, jws)
        return ResponseEntity.ok()
            .headers { headers -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()) }
            .body(responseBody)
    }

    @PublicEndpoint
    @PostMapping(value = ["/api/register"])
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<RegisterResponse> {
        val createUserDto = CreateUserDto(registerDto.username, registerDto.email, registerDto.password)
        val errors = CreateUserDtoValidator.validate(createUserDto)
        if(errors.isNotEmpty())
            return ResponseEntity.badRequest().build()

        val result = try {
            usermanagementClient.create(createUserDto)
        } catch (e: Exception) {
            null
        }

        if(result != null) {
            val responseBody = RegisterResponse(RegisterState.SUCCESS, registerDto.username, registerDto.email)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
        }
        val responseBody = RegisterResponse(RegisterState.ERROR, registerDto.username, registerDto.email)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody)
    }

    override fun publicKey(): PublicKeyDto {
        return authenticationService.publicKey
    }
}