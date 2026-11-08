package com.michibaum.poll_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "poll_submission")
class PollSubmission(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    val poll: Poll,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @OneToMany(mappedBy = "submission", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val answers: MutableList<QuestionAnswerValue> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
