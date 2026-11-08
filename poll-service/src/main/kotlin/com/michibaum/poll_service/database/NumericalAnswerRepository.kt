package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NumericalAnswerRepository : JpaRepository<NumericalAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<NumericalAnswer>
    fun findByQuestionId(questionId: UUID): List<NumericalAnswer>
}
