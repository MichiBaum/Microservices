package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "poll_invitee")
class PollInvitee(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    val poll: InvitePoll,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()

    @PrePersist
    fun onPrePersist() {
        createdAt = Instant.now()
    }
}
