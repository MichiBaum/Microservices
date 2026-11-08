package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "date_answer")
class DateAnswer(
    submission: PollSubmission,
    question: Question,
    @Column(name = "date_value", nullable = false)
    var dateValue: LocalDate,
    createdAt: Instant = Instant.now(),
    id: UUID? = null
) : QuestionAnswer(
    submission = submission,
    question = question,
    createdAt = createdAt,
    id = id
)
