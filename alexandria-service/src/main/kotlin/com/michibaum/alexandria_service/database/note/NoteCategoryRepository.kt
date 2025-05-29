package com.michibaum.alexandria_service.database.note

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NoteCategoryRepository : JpaRepository<NoteCategory, UUID> {
}