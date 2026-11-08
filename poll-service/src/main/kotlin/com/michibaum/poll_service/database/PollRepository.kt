package com.michibaum.poll_service.database

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PollRepository : JpaRepository<Poll, UUID> {
    fun findBySlug(slug: String): Poll?
    fun findBySlugAndDeletedAtIsNull(slug: String): Poll?
    fun findByIdAndDeletedAtIsNull(id: UUID): Poll?
    fun existsBySlug(slug: String): Boolean
    fun findAllByCreatorIdAndDeletedAtIsNull(creatorId: String, pageable: Pageable): Page<Poll>
    fun findAllByCreatorIdAndStatusAndDeletedAtIsNull(creatorId: String, status: PollStatus, pageable: Pageable): Page<Poll>
    fun findAllByCreatorIdAndTitleContainingIgnoreCaseAndDeletedAtIsNull(creatorId: String, title: String, pageable: Pageable): Page<Poll>
    fun findAllByCreatorIdAndStatusAndTitleContainingIgnoreCaseAndDeletedAtIsNull(creatorId: String, status: PollStatus, title: String, pageable: Pageable): Page<Poll>
}
