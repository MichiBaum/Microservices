package com.michibaum.poll_service.database

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "invite_poll")
class InvitePoll(
    title: String,
    description: String? = null,
    slug: String,
    creatorId: String,
    status: PollStatus = PollStatus.DRAFT,
    isAnonymous: Boolean = false,
    thankYouMessage: String? = null,
    redirectUrl: String? = null,
    expiresAt: Instant? = null,
    deletedAt: Instant? = null,
    sections: MutableList<Section> = mutableListOf(),
    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val invitees: MutableSet<PollInvitee> = mutableSetOf(),
    createdAt: Instant = Instant.now(),
    updatedAt: Instant = Instant.now(),
    id: UUID? = null
) : Poll(
    title = title,
    description = description,
    slug = slug,
    creatorId = creatorId,
    status = status,
    isAnonymous = isAnonymous,
    thankYouMessage = thankYouMessage,
    redirectUrl = redirectUrl,
    expiresAt = expiresAt,
    deletedAt = deletedAt,
    sections = sections,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id
) {
    override val accessPolicy: AccessPolicy
        get() = AccessPolicy.INVITED_ONLY
}
