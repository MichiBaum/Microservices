package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.app.account.AccountDto
import com.michibaum.chess_service.domain.Gender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.util.UUID

data class CreatePersonDto(
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Firstname must contain only alphabetic characters")
    val firstname: String,
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Lastname must contain only alphabetic characters")
    val lastname: String,
    val fideId: String? = null,
    val federation: String? = null,
    val birthDate: String? = null,
    val gender: Gender
)

data class PersonDto(
    val id: UUID,
    val firstname: String,
    val lastname: String,
    val fideId: String?,
    val federation: String? = null,
    val birthDate: String? = null,
    val gender: Gender,
    val accounts: Set<AccountDto> = emptySet(),
)

data class PersonSearchDto(
    val firstname: String,
    val lastname: String,
)