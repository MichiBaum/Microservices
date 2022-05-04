package com.michibaum.usermanagement_library

import org.springframework.web.bind.annotation.PostMapping

interface UserManagementEndpoints {

    @PostMapping(value = ["/checkPasword"])
    fun checkPassword(loginDto: LoginDto): Boolean

}