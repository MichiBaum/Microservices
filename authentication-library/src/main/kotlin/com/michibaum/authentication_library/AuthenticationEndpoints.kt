package com.michibaum.authentication_library

import org.springframework.web.bind.annotation.GetMapping

interface AuthenticationEndpoints {
    @GetMapping(value = ["/getAuthDetails"])
    fun publicKey(): PublicKeyDto
}