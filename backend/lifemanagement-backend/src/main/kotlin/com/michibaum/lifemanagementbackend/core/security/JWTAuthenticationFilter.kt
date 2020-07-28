package com.michibaum.lifemanagementbackend.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
    private val ownAuthenticationManager: AuthenticationManager, //TODO needs to be renamed, because of parent with same name
    private val userRepository: UserRepository,
    private val lastLoginUpdater: LastLoginUpdater
) : UsernamePasswordAuthenticationFilter() {

    @Value("\${application.version}") private val applicationVersion: String = ""

    init {
        setFilterProcessesUrl("/lifemanagement/api/login")
    }

    private data class LoginDto(val username: String = "", val password: String = "")
    private data class TokenDto(
        val headerName: String,
        val token: String,
        val expiresAt: Date,
        val permissions: List<String>,
        val username: String
    )

    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse?): Authentication? {
        val credentials: LoginDto = ObjectMapper().readValue(req.inputStream, LoginDto::class.java)

        lastLoginUpdater.update(credentials.username)

        return ownAuthenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.username,
                credentials.password,
                ArrayList()
            )
        )
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        chain: FilterChain?,
        auth: Authentication
    ) {
        val expiresAt = Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)
        val permissions: List<String> = auth.authorities.map { obj: GrantedAuthority -> obj.authority }
        val jwt = TokenDto(
            SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + createJWT(auth, expiresAt),
            expiresAt,
            permissions,
            (auth.principal as User).username
        )
        manipulateResponse(response, jwt)
    }

    private fun manipulateResponse(
        response: HttpServletResponse,
        jwt: TokenDto
    ) {
        response.status = HttpServletResponse.SC_OK
        response.addHeader("Content-Type", "application/json")
        response.writer.write(jacksonObjectMapper().writeValueAsString(jwt))
        response.writer.flush()
        response.writer.close()
    }

    private fun createJWT(
        auth: Authentication,
        expiresAt: Date
    ): String {
        return JWT.create()
            .withSubject(
                (auth.principal as User).username
            )
            .withClaim("backend_version", applicationVersion)
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC512(SecurityConstants.SECRET))
    }

}
