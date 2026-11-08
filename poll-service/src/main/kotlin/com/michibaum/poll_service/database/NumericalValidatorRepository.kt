package com.michibaum.poll_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NumericalValidatorRepository : JpaRepository<NumericalValidator, UUID> {
    fun findByQuestionId(questionId: UUID): NumericalValidator?
}
