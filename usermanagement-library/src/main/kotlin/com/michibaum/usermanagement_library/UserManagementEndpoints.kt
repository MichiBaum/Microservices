package com.michibaum.usermanagement_library

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface UserManagementEndpoints {

    @PostMapping(value = ["/checkUserDetails"])
    fun checkUserDetails(@RequestBody loginDto: LoginDto): UserDetailsDto?

}