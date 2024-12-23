package com.michibaum.usermanagement_service

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PermissionRepository: JpaRepository<Permission, String> {
}