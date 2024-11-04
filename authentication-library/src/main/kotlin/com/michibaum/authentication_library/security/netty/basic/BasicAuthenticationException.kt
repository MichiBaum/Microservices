package com.michibaum.authentication_library.security.netty.basic

import org.springframework.security.core.AuthenticationException

class BasicAuthenticationException(msg: String): AuthenticationException(msg) {
}