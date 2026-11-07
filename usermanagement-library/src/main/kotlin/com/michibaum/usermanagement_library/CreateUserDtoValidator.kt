package com.michibaum.usermanagement_library

class CreateUserDtoValidator {

    companion object {
        fun validate(dto: CreateUserDto): List<String>{
            val errors = mutableListOf<String>()
            if(dto.email.isBlank())
                errors.add("email")
            return errors
        }
    }

}