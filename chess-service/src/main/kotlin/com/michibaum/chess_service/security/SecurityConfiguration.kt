package com.michibaum.chess_service.security

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
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
                it
                    .requestMatchers(
                        HttpMethod.GET,"/api/events",
                        "/api/events/*",
                        "/api/events/*/participants",
                        "/api/events/*/games",
                        "/api/event-categories",
                        "/api/event-categories/with-events"
                    ).permitAll()
                    .requestMatchers(
                        "/actuator",
                        "/actuator/**"
                    ).hasAnyAuthority(Permissions.ADMIN_SERVICE.name)
                    .anyRequest().authenticated()
            }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { httpBasicSpec -> httpBasicSpec.disable() }
            .formLogin { formLoginSpec -> formLoginSpec.disable() }
            .csrf { csrfSpec -> csrfSpec.disable() }
            .logout { logoutSpec -> logoutSpec.disable() }
            .build()
    }

}