package com.michibaum.authentication_service.app.basic_authentication

import com.michibaum.authentication_service.database.BasicAuthenticationAttempt
import com.michibaum.authentication_service.database.BasicAuthenticationFailure
import com.michibaum.authentication_service.database.BasicAuthenticationRepository
import com.michibaum.authentication_service.database.BasicAuthenticationSuccess
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Service

@Service
class AuthenticationAttemptService(
    private val basicAuthenticationRepository: BasicAuthenticationRepository
) {

    @Observed(name = "basic-authentication-attempt")
    fun createAttempt(username: String): BasicAuthenticationAttempt {
        val authenticationResult = BasicAuthenticationAttempt(
            username = username
        )
        return basicAuthenticationRepository.save(authenticationResult)
    }

    @Observed(name = "basic-authentication-failed")
    fun attemptFailed(authenticationAttempt: BasicAuthenticationAttempt): BasicAuthenticationFailure {
        val newAuthenticationAttempt = BasicAuthenticationFailure.from(authenticationAttempt)
        val savedNewAuthenticationAttempt = basicAuthenticationRepository.save(newAuthenticationAttempt)
        basicAuthenticationRepository.delete(authenticationAttempt)
        return savedNewAuthenticationAttempt
    }

    @Observed(name = "basic-authentication-successful")
    fun attemptSuccessful(authenticationAttempt: BasicAuthenticationAttempt, userId: String, jws: String): BasicAuthenticationSuccess {
        val newAuthenticationAttempt = BasicAuthenticationSuccess.from(authenticationAttempt, userId,jws)
        val savedNewAuthenticationAttempt = basicAuthenticationRepository.save(newAuthenticationAttempt)
        basicAuthenticationRepository.delete(authenticationAttempt)
        return savedNewAuthenticationAttempt
    }


}