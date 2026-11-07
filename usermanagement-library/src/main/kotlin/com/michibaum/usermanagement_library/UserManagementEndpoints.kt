package com.michibaum.usermanagement_library

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface UserManagementEndpoints {

    @Deprecated(message = "Use authentication-service local verification and getUserDetailsByUsername")
    @PostMapping(value = ["/api/checkUserDetails"])
    fun checkUserDetails(@RequestBody loginDto: LoginDto): UserDetailsDto?

    @PostMapping("/api/users")
    fun create(@RequestBody createUserDto: CreateUserDto): UserDetailsDto?

    @GetMapping("/api/users/by-username/{username}/details")
    fun getUserDetailsByUsername(@PathVariable("username") username: String): UserDetailsDto?

    @DeleteMapping("/api/users/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void>

}