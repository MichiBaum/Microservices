package com.michibaum.usermanagement_service

import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository: JpaRepository<Permission, String> {
}