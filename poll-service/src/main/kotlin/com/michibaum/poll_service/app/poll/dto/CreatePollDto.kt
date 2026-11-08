package com.michibaum.poll_service.app.poll.dto

import com.michibaum.poll_service.database.AccessPolicy
import com.michibaum.poll_service.database.PollStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CreatePollDto(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    val accessPolicy: AccessPolicy = AccessPolicy.OPEN,
    val isAnonymous: Boolean = false,
    val expiresAt: Instant? = null,
    val status: PollStatus = PollStatus.DRAFT,
    val secret: String? = null,
    val inviteeUserIds: List<String> = emptyList(),
    @field:Valid
    val sections: List<CreateSectionDto> = emptyList()
)
