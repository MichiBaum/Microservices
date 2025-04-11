package com.michibaum.alexandria_service.database.news

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NewsTagRepository : JpaRepository<NewsTag, UUID> {
}