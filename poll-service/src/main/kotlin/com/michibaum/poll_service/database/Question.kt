package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "poll_question")
class Question(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    val section: Section,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: QuestionType,

    @Column(name = "is_required", nullable = false)
    var isRequired: Boolean = false,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @OneToOne(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var validator: QuestionValidator? = null,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val options: MutableList<QuestionOption> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
