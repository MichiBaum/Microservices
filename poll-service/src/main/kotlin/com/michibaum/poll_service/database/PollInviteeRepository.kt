package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PollInviteeRepository : JpaRepository<PollInvitee, UUID> {
    fun existsByPollIdAndUserId(pollId: UUID, userId: String): Boolean
    fun findAllByUserId(userId: String): List<PollInvitee>
    fun findAllByPollId(pollId: UUID): List<PollInvitee>
}
