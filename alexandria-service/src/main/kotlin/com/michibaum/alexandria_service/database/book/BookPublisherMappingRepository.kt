package com.michibaum.alexandria_service.database.book

import org.springframework.data.jpa.repository.JpaRepository

interface BookPublisherMappingRepository : JpaRepository<BookPublisherMapping, BookPublisherMappingId> {
}