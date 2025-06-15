package com.michibaum.usermanagement_library

class CreateUserDtoValidator {

    companion object {
        fun validate(dto: CreateUserDto): List<String>{
            val errors = mutableListOf<String>()
            if(dto.username.isBlank())
                errors.add("username")
            if(dto.email.isBlank())
                errors.add("email")
            if(dto.password.isBlank())
                errors.add("password")
            return errors
        }
    }

}