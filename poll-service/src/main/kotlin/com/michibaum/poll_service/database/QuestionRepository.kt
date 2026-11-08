package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuestionRepository : JpaRepository<Question, UUID> {
    fun findAllBySectionIdOrderByOrderIndexAsc(sectionId: UUID): List<Question>
}
