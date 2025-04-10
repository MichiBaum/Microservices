package com.michibaum.alexandria_service.database.note

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NoteCategoryRepository : JpaRepository<NoteCategory, UUID> {
}