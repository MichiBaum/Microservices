package com.michibaum.authentication_library.security.jwt.netty

import com.michibaum.authentication_library.JwsWrapper
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class JwtAuthenticationConverter: ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { header -> header.startsWith("Bearer ") }
            .map { header -> header.substring("Bearer ".length) }
            .map { token -> JwtAuthentication(token, createUserDetails(token)) }
    }

    private fun createUserDetails(token: String): UserDetails {
        val username: String = JwsWrapper(token).getUsername()
        return User.builder()
            .username(username)
            .authorities(createAuthorities(token))
            .password("")
            .build()
    }

    private fun createAuthorities(token: String): List<SimpleGrantedAuthority> {
        return JwsWrapper(token).getPermissions().stream()
            .map { permission -> "$permission" }
            .map { permission: String? -> SimpleGrantedAuthority(permission) }
            .toList()
    }

}