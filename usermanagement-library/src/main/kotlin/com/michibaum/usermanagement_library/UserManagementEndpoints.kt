package com.michibaum.usermanagement_library

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

interface UserManagementEndpoints {

    @PostMapping(value = ["/api/checkUserDetails"])
    fun checkUserDetails(@RequestBody @Valid loginDto: LoginDto): UserDetailsDto?

    @PostMapping("/api/users")
    fun create(@RequestBody @Valid createUserDto: CreateUserDto): UserDetailsDto?

}