package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "question_validator")
class QuestionValidator(
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    val question: Question,

    @Column(name = "min_length", nullable = true)
    var minLength: Int? = null,

    @Column(name = "max_length", nullable = true)
    var maxLength: Int? = null,

    @Column(nullable = true, columnDefinition = "TEXT")
    var pattern: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "format_preset", nullable = true)
    var formatPreset: FormatPreset? = null,

    @Column(name = "min_value", nullable = true)
    var minValue: Double? = null,

    @Column(name = "max_value", nullable = true)
    var maxValue: Double? = null,

    @Column(name = "integer_only", nullable = true)
    var integerOnly: Boolean? = null,

    @Column(name = "min_date", nullable = true)
    var minDate: LocalDate? = null,

    @Column(name = "max_date", nullable = true)
    var maxDate: LocalDate? = null,

    @Column(name = "disable_past", nullable = true)
    var disablePast: Boolean? = null,

    @Column(name = "min_items", nullable = true)
    var minItems: Int? = null,

    @Column(name = "max_items", nullable = true)
    var maxItems: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
