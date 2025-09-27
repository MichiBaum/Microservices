package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.app.account.AccountDto
import com.michibaum.chess_service.database.Gender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.util.*

data class WritePersonDto(
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Firstname must contain only alphabetic characters")
    val firstname: String,
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Lastname must contain only alphabetic characters")
    val lastname: String,
    val federation: String? = null,
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\$")
    val birthday: String? = null,
    val gender: Gender
)

data class PersonDto(
    val id: UUID,
    val firstname: String,
    val lastname: String,
    val federation: String? = null,
    val birthday: String? = null,
    val gender: Gender,
    val accounts: Set<AccountDto> = emptySet(),
)

data class PersonSearchDto(
    val firstname: String,
    val lastname: String,
)