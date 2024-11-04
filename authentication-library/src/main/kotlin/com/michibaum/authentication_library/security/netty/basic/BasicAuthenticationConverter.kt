package com.michibaum.authentication_library.security.netty.basic

import com.michibaum.permission_library.Permissions
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

class BasicAuthenticationConverter: ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { header -> header.startsWith("Basic ") }
            .map { header -> header.substring("Basic ".length) }
            .map { token -> BasicAuthentication(createUserDetails(token)) }
    }

    private fun createUserDetails(token: String): UserDetails {
        val decoded = Base64.getDecoder().decode(token)
        val usernameAndPassword = String(decoded).split(":")
        return User.builder()
            .username(usernameAndPassword[0])
            .authorities(SimpleGrantedAuthority(Permissions.ADMIN_SERVICE.name))
            .password(usernameAndPassword[1])
            .build()
    }
}