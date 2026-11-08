package com.michibaum.poll_service.database

import jakarta.persistence.*
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
    val answers: MutableList<QuestionAnswer> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()

    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }
}
