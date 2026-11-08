package com.michibaum.poll_service.app.poll.dto

import com.michibaum.poll_service.database.FormatPreset
import java.time.LocalDate

data class ValidatorDto(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val formatPreset: FormatPreset? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val integerOnly: Boolean? = null,
    val minDate: LocalDate? = null,
    val maxDate: LocalDate? = null,
    val disablePast: Boolean? = null,
    val minSelected: Int? = null,
    val maxSelected: Int? = null,
    val minItems: Int? = null,
    val maxItems: Int? = null
)
