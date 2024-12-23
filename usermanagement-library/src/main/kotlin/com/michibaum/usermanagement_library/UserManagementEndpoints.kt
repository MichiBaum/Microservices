package com.michibaum.usermanagement_library

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface UserManagementEndpoints {

    @PostMapping(value = ["/api/checkUserDetails"])
    fun checkUserDetails(@RequestBody loginDto: LoginDto): UserDetailsDto?

    @PostMapping("/api/users")
    fun create(@RequestBody createUserDto: CreateUserDto): UserDetailsDto?

}