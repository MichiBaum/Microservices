package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SingleSelectAnswerRepository : JpaRepository<SingleSelectAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<SingleSelectAnswer>
    fun findByQuestionId(questionId: UUID): List<SingleSelectAnswer>
}
