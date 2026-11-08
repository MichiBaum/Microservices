package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DateAnswerRepository : JpaRepository<DateAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<DateAnswer>
    fun findByQuestionId(questionId: UUID): List<DateAnswer>
}
