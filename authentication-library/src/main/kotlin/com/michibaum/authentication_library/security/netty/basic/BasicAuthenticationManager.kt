package com.michibaum.authentication_library.security.netty.basic

import com.michibaum.authentication_library.security.netty.SpecificAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class BasicAuthenticationManager(private val credentialsValidator: CredentialsValidator): SpecificAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .cast(BasicAuthentication::class.java)
            .filter { basic: BasicAuthentication -> credentialsValidator.validate(basic) }
            .map { basic: BasicAuthentication -> basic.apply { isAuthenticated = true } }
            .switchIfEmpty(Mono.error(BasicAuthenticationException("Invalid token.")))
            .cast(Authentication::class.java)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BasicAuthentication::class.java.isAssignableFrom(authentication)
    }

//    private fun validateCredentials(basic: BasicAuthentication): Boolean {
//        val validUsername = basic.getUsername().equals(adminServiceProperties.username)
//        val validPassword = basic.getPassword().equals(adminServiceProperties.password)
//        return validUsername && validPassword
//    }

}