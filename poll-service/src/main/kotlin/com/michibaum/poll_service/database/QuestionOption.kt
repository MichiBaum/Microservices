package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "question_option")
class QuestionOption(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
