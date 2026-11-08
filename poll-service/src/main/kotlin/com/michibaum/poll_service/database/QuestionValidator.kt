package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class QuestionValidator(
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
