package com.michibaum.alexandria_service.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AuthorRepository : JpaRepository<Author, UUID> {
}