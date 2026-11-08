package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "multi_select_validator")
class MultiSelectValidator(
    question: Question,

    @Column(name = "min_selected", nullable = true)
    var minSelected: Int? = null,

    @Column(name = "max_selected", nullable = true)
    var maxSelected: Int? = null,

    id: UUID? = null
) : QuestionValidator(
    question = question,
    id = id
)
