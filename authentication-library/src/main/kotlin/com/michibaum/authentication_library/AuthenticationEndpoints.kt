package com.michibaum.authentication_library

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

interface AuthenticationEndpoints {
    @GetMapping(value = ["/getAuthDetails"])
    fun publicKey(): PublicKeyDto
}