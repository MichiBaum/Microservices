package com.michibaum.authentication_library

import org.springframework.web.bind.annotation.PostMapping

interface AuthenticationEndpoints {
    @get:PostMapping(value = ["/checkPasword"])
    val publicKey: PublicKeyDto?
}