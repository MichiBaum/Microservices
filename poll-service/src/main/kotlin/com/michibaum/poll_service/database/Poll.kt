package com.michibaum.poll_service.database

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Poll(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, length = 64)
    var slug: String,

    @Column(name = "creator_id", nullable = false)
    val creatorId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PollStatus = PollStatus.DRAFT,

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

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    abstract val accessPolicy: AccessPolicy

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
