package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "text_answer")
class TextAnswer(
    submission: PollSubmission,
    question: Question,
    @Column(name = "text_value", nullable = false, columnDefinition = "TEXT")
    var textValue: String,
    createdAt: Instant = Instant.now(),
    id: UUID? = null
) : QuestionAnswer(
    submission = submission,
    question = question,
    createdAt = createdAt,
    id = id
)
