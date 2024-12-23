package com.michibaum.usermanagement_library

class CreateUserDtoValidator {

    companion object {
        fun validate(dto: CreateUserDto): List<String>{
            val errors = mutableListOf<String>()
            if(dto.username.isEmpty())
                errors.add("username")
            if(dto.email.isEmpty())
                errors.add("email")
            if(dto.password.isEmpty())
                errors.add("password")
            return errors
        }
    }

}