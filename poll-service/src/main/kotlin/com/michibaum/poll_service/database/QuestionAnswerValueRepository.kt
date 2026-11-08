package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface QuestionAnswerValueRepository : JpaRepository<QuestionAnswerValue, UUID> {
    fun findAllBySubmissionId(submissionId: UUID): List<QuestionAnswerValue>
    fun findAllByQuestionId(questionId: UUID): List<QuestionAnswerValue>
}
