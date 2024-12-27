package com.michibaum.music_service.security

import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.permission_library.Permissions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter


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
                        "/api/spotify/auth",
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