package com.michibaum.lifemanagementbackend.core.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
    private val ownAuthenticationManager: AuthenticationManager, //TODO needs to be renamed, because of parent with same name
    private val lastLoginUpdater: LastLoginUpdater,
    private val loginLogCreator: LoginLogCreator,
    private val jwtFactory: JWTFactory
) : UsernamePasswordAuthenticationFilter() {

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

    @Throws(DisabledException::class, LockedException::class, BadCredentialsException::class)
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse?): Authentication? {
        val credentials: LoginDto = ObjectMapper().readValue(req.inputStream, LoginDto::class.java)

        try {
            return ownAuthenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    credentials.username,
                    credentials.password,
                    ArrayList()
                )
            )
        }
        catch (dex: DisabledException){ /*throw dex*/ }
        catch (lex: LockedException){ /*throw lex*/ }
        catch (bcex: BadCredentialsException){ /*throw bcex*/ }
        return null
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        chain: FilterChain?,
        auth: Authentication
    ) {
        val expiresAt = Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)
        val permissions: List<String> = auth.authorities.map { obj: GrantedAuthority -> obj.authority }
        val realjwt = createJWT(auth)
        val responseJwt = TokenDto(
            SecurityConstants.HEADER_STRING,
            SecurityConstants.TOKEN_PREFIX + realjwt,
            expiresAt,
            permissions,
            (auth.principal as User).username
        )

        val user = lastLoginUpdater.update((auth.principal as User).username)
        user?.let { loginLogCreator.create(it, request, true, realjwt, expiresAt) }

        manipulateResponseSuccessfullAuth(response, responseJwt)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException
    ) {

        SecurityContextHolder.clearContext()
        manipulateResponseBadAuth(response)
        rememberMeServices.loginFail(request, response)

    }

    private fun manipulateResponseSuccessfullAuth(
        response: HttpServletResponse,
        jwt: TokenDto
    ) {
        response.status = HttpServletResponse.SC_OK
        response.addHeader("Content-Type", "application/json")
        response.writer.write(jacksonObjectMapper().writeValueAsString(jwt))
        response.writer.flush()
        response.writer.close()
    }

    private fun manipulateResponseBadAuth(
        response: HttpServletResponse?
    ) {
        response?.let {
            it.status = HttpServletResponse.SC_UNAUTHORIZED
            it.writer.flush()
            it.writer.close()
        }
    }

    private fun createJWT(auth: Authentication): String =
        jwtFactory.create((auth.principal as User).username)
        ?.sign(JWTFactory.Companion.ALGORITHMS.HMAC512)
        ?: ""

}
