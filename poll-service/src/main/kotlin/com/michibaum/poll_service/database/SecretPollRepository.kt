package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SecretPollRepository : JpaRepository<SecretPoll, UUID> {
    fun findBySlug(slug: String): SecretPoll?
    fun findByCreatorId(creatorId: String): List<SecretPoll>
}
