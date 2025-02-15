package com.michibaum.authentication_service.security

import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.authentication_library.public_endpoints.PublicEndpointResolver
import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.authentication_library.security.ServletDelegateAuthenticationManager
import com.michibaum.authentication_library.security.SpecificAuthenticationManager
import com.michibaum.authentication_library.security.basic.BasicAuthenticationManager
import com.michibaum.authentication_library.security.basic.CredentialsValidator
import com.michibaum.authentication_library.security.basic.BasicAuthenticationConverter
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationManager
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationConverter
import com.michibaum.authentication_service.authentication.AuthenticationService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationConverter

@Configuration
@EnableConfigurationProperties(value = [AdminServiceCredentials::class])
class SecurityBeansConfiguration {
    @Bean
    fun publicEndpointResolver(): PublicEndpointResolver =
        PublicEndpointResolver(PublicEndpoint::class.java, "com.michibaum.authentication_service")



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
    fun authenticationManager(specificAuthenticationManagers: List<SpecificAuthenticationManager>): AuthenticationManager =
        ServletDelegateAuthenticationManager(specificAuthenticationManagers)



    @Bean
    fun jwtAuthenticationConverter(): AuthenticationConverter =
        JwtAuthenticationConverter()

    @Bean
    fun basicAuthenticationConverter(): AuthenticationConverter =
        BasicAuthenticationConverter()



    @Bean
    fun authenticationFilter(authenticationManager: AuthenticationManager, authenticationConverters: List<AuthenticationConverter>) =
        ServletAuthenticationFilter(authenticationManager, authenticationConverters)
}