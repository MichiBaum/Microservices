package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_service.database.AuthenticationAttempt
import com.michibaum.authentication_service.database.AuthenticationAttemptRepository
import com.michibaum.authentication_service.database.JwtEntity
import com.michibaum.authentication_service.database.JwtRepository
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationRepository
import com.michibaum.authentication_service.database.basic_authentication.BasicAuthenticationSuccess
import io.micrometer.observation.annotation.Observed
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AuthenticationAttemptService(
    private val basicAuthenticationRepository: BasicAuthenticationRepository,
    private val authenticationAttemptRepository: AuthenticationAttemptRepository,
    private val jwtRepository: JwtRepository
) {

    @Transactional
    @Observed(name = "basic-authentication-attempt")
    fun createAttempt(username: String): BasicAuthenticationAttempt {
        val authenticationAttempt = authenticationAttemptRepository.save(AuthenticationAttempt())
        val basicAuthenticationAttempt = BasicAuthenticationAttempt(
            username = username,
            authenticationAttempt = authenticationAttempt
        )
        return basicAuthenticationRepository.save(basicAuthenticationAttempt)
    }

    @Transactional
    @Observed(name = "basic-authentication-failed")
    fun attemptFailed(authenticationAttempt: BasicAuthenticationAttempt): BasicAuthenticationFailure {
        val newAuthenticationAttempt = BasicAuthenticationFailure.from(authenticationAttempt)
        basicAuthenticationRepository.delete(authenticationAttempt)
        val savedNewAuthenticationAttempt = basicAuthenticationRepository.save(newAuthenticationAttempt)
        return savedNewAuthenticationAttempt
    }

    @Transactional
    @Observed(name = "basic-authentication-successful")
    fun attemptSuccessful(authenticationAttempt: BasicAuthenticationAttempt, username: String, jws: String): BasicAuthenticationSuccess {
        val jwt = jwtRepository.save(JwtEntity(jwt = jws))
        val newAuthenticationAttempt = BasicAuthenticationSuccess.from(authenticationAttempt, username,jwt)
        basicAuthenticationRepository.delete(authenticationAttempt)
        val savedNewAuthenticationAttempt = basicAuthenticationRepository.save(newAuthenticationAttempt)
        return savedNewAuthenticationAttempt
    }


}