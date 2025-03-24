package com.michibaum.authentication_library.security.jwt

import org.springframework.security.authentication.BadCredentialsException

class InvalidJwtException: BadCredentialsException("Invalid JWT token") {
}