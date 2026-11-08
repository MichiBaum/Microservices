package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "numerical_validator")
class NumericalValidator(
    question: Question,

    @Column(name = "min_value", nullable = true)
    var minValue: Double? = null,

    @Column(name = "max_value", nullable = true)
    var maxValue: Double? = null,

    @Column(name = "integer_only", nullable = false)
    var integerOnly: Boolean = false,

    id: UUID? = null
) : QuestionValidator(
    question = question,
    id = id
)
