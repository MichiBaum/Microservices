package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TextValidatorRepository : JpaRepository<TextValidator, UUID> {
    fun findByQuestionId(questionId: UUID): TextValidator?
}
