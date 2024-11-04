package com.michibaum.authentication_library.security.netty

import org.springframework.security.core.AuthenticationException

class NoAuthenticationManagerException(msg: String): AuthenticationException(msg) {
}