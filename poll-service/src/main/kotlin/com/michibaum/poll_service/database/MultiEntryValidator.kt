package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "multi_entry_validator")
class MultiEntryValidator(
    question: Question,

    @Column(name = "min_items", nullable = true)
    var minItems: Int? = null,

    @Column(name = "max_items", nullable = true)
    var maxItems: Int? = null,

    id: UUID? = null
) : QuestionValidator(
    question = question,
    id = id
)
