package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "question_answer_value")
class QuestionAnswerValue(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    val submission: PollSubmission,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "option_id", nullable = true)
    var option: QuestionOption? = null,

    @Column(name = "text_value", nullable = true, columnDefinition = "TEXT")
    var textValue: String? = null,

    @Column(name = "numeric_value", nullable = true)
    var numericValue: Double? = null,

    @Column(name = "date_value", nullable = true)
    var dateValue: LocalDate? = null,

    @Column(name = "order_index", nullable = true)
    var orderIndex: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
