package com.michibaum.authentication_service.security

import com.michibaum.permission_library.Permissions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.*
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationWebFilter: AuthenticationWebFilter,
        basicAuthenticationWebFilter: AuthenticationWebFilter,
    ): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
                exchanges
                    .pathMatchers(
                        "/api/authenticate",
                        "/api/getAuthDetails",
                        "/api/logout"
                    ).permitAll()
                    .pathMatchers(
                        "/actuator",
                        "/actuator/**"
                    ).hasAnyAuthority(Permissions.ADMIN_SERVICE.name)
                    .anyExchange().authenticated()
            }
            .addFilterAt(basicAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic { httpBasicSpec -> httpBasicSpec.disable() }
            .formLogin { formLoginSpec -> formLoginSpec.disable() }
            .csrf { csrfSpec -> csrfSpec.disable() }
            .logout { logoutSpec -> logoutSpec.disable() }
            .build()
    }

}