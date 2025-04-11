package com.michibaum.alexandria_service.database.magazine

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MagazineRepository : JpaRepository<Magazine, UUID> {
}