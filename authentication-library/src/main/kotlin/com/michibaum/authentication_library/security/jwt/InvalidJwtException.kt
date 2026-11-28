package com.michibaum.authentication_library.security.jwt

import org.springframework.security.authentication.BadCredentialsException

class InvalidJwtException : BadCredentialsException {
    constructor(message: String = "Invalid JWT token") : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}