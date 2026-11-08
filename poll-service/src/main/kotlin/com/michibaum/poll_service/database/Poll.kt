package com.michibaum.poll_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "poll")
class Poll(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, unique = true, length = 64)
    var slug: String,

    @Column(name = "creator_id", nullable = false)
    val creatorId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PollStatus = PollStatus.DRAFT,

    @Enumerated(EnumType.STRING)
    @Column(name = "access_policy", nullable = false)
    var accessPolicy: AccessPolicy = AccessPolicy.OPEN,

    @Column(nullable = true)
    var secret: String? = null,

    @Column(name = "is_anonymous", nullable = false)
    var isAnonymous: Boolean = false,

    @Column(name = "thank_you_message", nullable = true, columnDefinition = "TEXT")
    var thankYouMessage: String? = null,

    @Column(name = "redirect_url", nullable = true, length = 2048)
    var redirectUrl: String? = null,

    @Column(name = "expires_at", nullable = true)
    var expiresAt: Instant? = null,

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: Instant? = null,

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val sections: MutableList<Section> = mutableListOf(),

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val invitees: MutableSet<PollInvitee> = mutableSetOf(),

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
