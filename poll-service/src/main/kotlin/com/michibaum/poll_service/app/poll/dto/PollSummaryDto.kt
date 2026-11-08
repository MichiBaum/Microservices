package com.michibaum.poll_service.app.poll.dto

import com.michibaum.poll_service.database.AccessPolicy
import com.michibaum.poll_service.database.PollStatus
import java.time.Instant
import java.util.UUID

data class PollSummaryDto(
    val id: UUID,
    val title: String,
    val slug: String,
    val description: String?,
    val status: PollStatus,
    val accessPolicy: AccessPolicy,
    val isAnonymous: Boolean,
    val expiresAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val submissionCount: Long
)
