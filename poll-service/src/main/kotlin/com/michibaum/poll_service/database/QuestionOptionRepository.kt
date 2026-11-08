package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuestionOptionRepository : JpaRepository<QuestionOption, UUID> {
    fun findAllByQuestionIdOrderByOrderIndexAsc(questionId: UUID): List<QuestionOption>
}
