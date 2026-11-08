package com.michibaum.poll_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "multi_entry_answer")
class MultiEntryAnswer(
    submission: PollSubmission,
    question: Question,
    @Column(name = "text_value", nullable = false, columnDefinition = "TEXT")
    var textValue: String,
    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,
    createdAt: Instant = Instant.now(),
    id: UUID? = null
) : QuestionAnswer(
    submission = submission,
    question = question,
    createdAt = createdAt,
    id = id
)
