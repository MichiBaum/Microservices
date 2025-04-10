package com.michibaum.alexandria_service.database.book

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BookPublisherRepository : JpaRepository<BookPublisher, UUID> {
}