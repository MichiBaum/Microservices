package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuestionAnswerRepository : JpaRepository<QuestionAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<QuestionAnswer>
    fun findByQuestionId(questionId: UUID): List<QuestionAnswer>
}
