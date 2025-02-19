package com.michibaum.registry_service.security

import com.michibaum.authentication_library.public_endpoints.PublicEndpointResolver
import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.authentication_starter.AuthenticationAutoConfiguration
import com.michibaum.permission_library.Permissions
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationFilter: ServletAuthenticationFilter,
        publicEndpointResolver: PublicEndpointResolver
    ): SecurityFilterChain {
        val publicEndpoints = publicEndpointResolver.run()
        return http
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(*publicEndpoints.map { it.requestMatcher}.toTypedArray()).permitAll()
                    .requestMatchers(
                        "/actuator",
                        "/actuator/**"
                    ).hasAnyAuthority(Permissions.ADMIN_SERVICE.name)
                    .anyRequest().hasAnyAuthority(Permissions.ADMIN_SERVICE.name)
            }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { httpBasicSpec -> httpBasicSpec.disable() }
            .formLogin { formLoginSpec -> formLoginSpec.disable() }
            .csrf { csrfSpec -> csrfSpec.disable() }
            .logout { logoutSpec -> logoutSpec.disable() }
            .sessionManagement { sessionManagementSpec -> sessionManagementSpec.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS) }
            .build()
    }

}