package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DateValidatorRepository : JpaRepository<DateValidator, UUID> {
    fun findByQuestionId(questionId: UUID): DateValidator?
}
