package com.michibaum.authentication_service.app.basic_authentication

class AuthenticationDtoValidator {

    companion object {
        fun validate(dto: AuthenticationDto): List<String> {
            val errors = mutableListOf<String>()
            if (dto.username.isBlank())
                errors.add("username")
            if (dto.password.isBlank())
                errors.add("password")
            return errors
        }
    }

}
