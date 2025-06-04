package com.michibaum.usermanagement_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PermissionRepository: JpaRepository<Permission, String> {
    
    @Query("SELECT p FROM Permission p JOIN UserPermissionMapping upm ON upm.permission = p WHERE upm.user = :user")
    fun findByUser(user: User): List<Permission>
    
}