package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MultiEntryAnswerRepository : JpaRepository<MultiEntryAnswer, UUID> {
    fun findBySubmissionId(submissionId: UUID): List<MultiEntryAnswer>
    fun findByQuestionId(questionId: UUID): List<MultiEntryAnswer>
}
