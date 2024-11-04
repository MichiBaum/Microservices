package com.michibaum.fitness_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.security.netty.DelegateAuthenticationManager
import com.michibaum.authentication_library.security.netty.SpecificAuthenticationManager
import com.michibaum.authentication_library.security.netty.basic.BasicAuthenticationConverter
import com.michibaum.authentication_library.security.netty.basic.BasicAuthenticationManager
import com.michibaum.authentication_library.security.netty.basic.CredentialsValidator
import com.michibaum.authentication_library.security.netty.jwt.JwsValidator
import com.michibaum.authentication_library.security.netty.jwt.JwtAuthenticationConverter
import com.michibaum.authentication_library.security.netty.jwt.JwtAuthenticationManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
class SecurityBeansConfiguration {

    @Bean
    fun adminServiceCredentials(): AdminServiceCredentials =
        AdminServiceCredentials()



    @Bean
    fun jwsValidator(@Lazy authenticationClient: AuthenticationClient): JwsValidator =
        JwsValidator(authenticationClient)

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
        DelegateAuthenticationManager(specificAuthenticationManagers)



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
            setServerAuthenticationConverter(jwtAuthenticationConverter)
        }

    @Bean
    fun basicAuthenticationWebFilter(
        authenticationManager: ReactiveAuthenticationManager,
        basicAuthenticationConverter: BasicAuthenticationConverter
    ) =
        AuthenticationWebFilter(authenticationManager).apply {
            setServerAuthenticationConverter(basicAuthenticationConverter)
        }

}