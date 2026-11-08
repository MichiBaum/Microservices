package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "date_validator")
class DateValidator(
    question: Question,

    @Column(name = "min_date", nullable = true)
    var minDate: LocalDate? = null,

    @Column(name = "max_date", nullable = true)
    var maxDate: LocalDate? = null,

    @Column(name = "disable_past", nullable = false)
    var disablePast: Boolean = false,

    id: UUID? = null
) : QuestionValidator(
    question = question,
    id = id
)
