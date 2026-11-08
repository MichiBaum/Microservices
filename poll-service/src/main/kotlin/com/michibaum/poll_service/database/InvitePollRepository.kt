package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InvitePollRepository : JpaRepository<InvitePoll, UUID> {
    fun findBySlug(slug: String): InvitePoll?
    fun findByCreatorId(creatorId: String): List<InvitePoll>
}
