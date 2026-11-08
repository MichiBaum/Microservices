package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "text_validator")
class TextValidator(
    question: Question,

    @Column(name = "min_length", nullable = true)
    var minLength: Int? = null,

    @Column(name = "max_length", nullable = true)
    var maxLength: Int? = null,

    @Column(nullable = true, columnDefinition = "TEXT")
    var pattern: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "format_preset", nullable = true)
    var formatPreset: FormatPreset? = null,

    id: UUID? = null
) : QuestionValidator(
    question = question,
    id = id
)
