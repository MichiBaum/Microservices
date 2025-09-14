package com.michibaum.authentication_library.security

import org.springframework.security.core.AuthenticationException

class EmptyAuthenticationException(msg: String): AuthenticationException(msg) 