package com.michibaum.registry_service.security

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        authenticationFilter: ServletAuthenticationFilter
    ): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/actuator",
                    "/actuator/**"
                ).hasAnyAuthority(Permissions.ADMIN_SERVICE.name)
                .anyRequest().permitAll()
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