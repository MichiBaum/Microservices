package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PollSubmissionRepository : JpaRepository<PollSubmission, UUID> {
    fun findByPollIdAndUserId(pollId: UUID, userId: String): PollSubmission?
    fun existsByPollIdAndUserId(pollId: UUID, userId: String): Boolean
    fun countByPollId(pollId: UUID): Long
    fun findAllByPollId(pollId: UUID): List<PollSubmission>
}
