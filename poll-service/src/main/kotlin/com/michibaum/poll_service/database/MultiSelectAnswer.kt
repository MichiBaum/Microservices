package com.michibaum.poll_service.database

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "multi_select_answer")
class MultiSelectAnswer(
    submission: PollSubmission,
    question: Question,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "selected_option_id", nullable = false)
    var selectedOption: QuestionOption,
    createdAt: Instant = Instant.now(),
    id: UUID? = null
) : QuestionAnswer(
    submission = submission,
    question = question,
    createdAt = createdAt,
    id = id
)
