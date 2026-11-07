package com.michibaum.authentication_service.app.registration

class RegisterDtoValidator {

    companion object {
        fun validate(dto: RegisterDto): List<String> {
            val errors = mutableListOf<String>()
            if (dto.username.isBlank())
                errors.add("username")
            if (dto.email.isBlank())
                errors.add("email")
            if (dto.password.isBlank())
                errors.add("password")
            return errors
        }
    }

}
