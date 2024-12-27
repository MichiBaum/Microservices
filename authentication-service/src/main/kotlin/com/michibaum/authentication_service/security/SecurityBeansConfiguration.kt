package com.michibaum.authentication_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.security.ReactiveDelegateAuthenticationManager
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import com.michibaum.authentication_library.security.basic.BasicAuthenticationManager
import com.michibaum.authentication_library.security.basic.BasicExchangeMatcher
import com.michibaum.authentication_library.security.basic.CredentialsValidator
import com.michibaum.authentication_library.security.basic.netty.BasicAuthenticationConverter
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationManager
import com.michibaum.authentication_library.security.jwt.JwtExchangeMatcher
import com.michibaum.authentication_library.security.jwt.netty.JwtAuthenticationConverter
import com.michibaum.authentication_service.authentication.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
class SecurityBeansConfiguration {

    @Bean
    fun adminServiceCredentials(): AdminServiceCredentials =
        AdminServiceCredentials()



    @Bean
    fun jwsValidator(authenticationService: AuthenticationService): JwsValidator {
        val authenticationClient = object: AuthenticationClient{
            override fun publicKey(): PublicKeyDto {
                return authenticationService.publicKey
            }
        }
        return JwsValidator(authenticationClient)
    }

    @Bean
    fun credentialsValidator(adminServiceCredentials: AdminServiceCredentials): CredentialsValidator =
        CredentialsValidator { basic ->
            adminServiceCredentials.username == basic.getUsername() &&
                    adminServiceCredentials.password == basic.getPassword()
        }



    @Bean
    fun jwtAuthenticationManager(jwsValidator: JwsValidator): SpecificAuthenticationManager =
        JwtAuthenticationManager(jwsValidator)

    @Bean
    fun basicAuthenticationManager(credentialsValidator: CredentialsValidator): SpecificAuthenticationManager =
        BasicAuthenticationManager(credentialsValidator)

    @Bean
    fun authenticationManager(specificAuthenticationManagers: List<SpecificAuthenticationManager>): ReactiveAuthenticationManager =
        ReactiveDelegateAuthenticationManager(specificAuthenticationManagers)



    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter =
        JwtAuthenticationConverter()

    @Bean
    fun basicAuthenticationConverter(): BasicAuthenticationConverter =
        BasicAuthenticationConverter()



    @Bean
    fun jwtAuthenticationWebFilter(
        authenticationManager: ReactiveAuthenticationManager,
        jwtAuthenticationConverter: JwtAuthenticationConverter
    ) =
        AuthenticationWebFilter(authenticationManager).apply {
            setRequiresAuthenticationMatcher(JwtExchangeMatcher())
            setServerAuthenticationConverter(jwtAuthenticationConverter)
        }

    @Bean
    fun basicAuthenticationWebFilter(
        authenticationManager: ReactiveAuthenticationManager,
        basicAuthenticationConverter: BasicAuthenticationConverter
    ) =
        AuthenticationWebFilter(authenticationManager).apply {
            setRequiresAuthenticationMatcher(BasicExchangeMatcher())
            setServerAuthenticationConverter(basicAuthenticationConverter)
        }

}