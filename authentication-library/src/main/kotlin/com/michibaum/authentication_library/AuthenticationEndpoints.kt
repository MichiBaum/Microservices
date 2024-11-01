package com.michibaum.authentication_library

import org.springframework.web.bind.annotation.GetMapping

interface AuthenticationEndpoints {
    @GetMapping(value = ["/api/getAuthDetails"])
    fun publicKey(): PublicKeyDto
}