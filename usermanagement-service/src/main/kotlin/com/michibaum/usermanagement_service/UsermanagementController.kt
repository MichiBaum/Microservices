package com.michibaum.usermanagement_service

import org.springframework.web.bind.annotation.RestController
import lombok.RequiredArgsConstructor
import com.michibaum.usermanagement_library.UserManagementEndpoints
import com.michibaum.usermanagement_library.LoginDto

@RestController
@RequiredArgsConstructor
class UsermanagementController : UserManagementEndpoints {

    override fun checkPassword(loginDto: LoginDto): Boolean {
        return true
    }
}