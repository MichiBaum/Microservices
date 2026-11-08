package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "poll_section")
class Section(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    val poll: Poll,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @OneToMany(mappedBy = "section", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val questions: MutableList<Question> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
