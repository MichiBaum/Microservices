package com.michibaum.usermanagement_library

class LoginDtoValidator {

    companion object {
        fun validate(loginDto: LoginDto): List<String> {
            val errors = mutableListOf<String>()
            if (loginDto.username.isEmpty())
                errors.add("username")
            if (loginDto.password.isEmpty())
                errors.add("password")
            return errors
        }
    }

}