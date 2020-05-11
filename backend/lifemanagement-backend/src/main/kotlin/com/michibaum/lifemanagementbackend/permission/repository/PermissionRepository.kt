package com.michibaum.lifemanagementbackend.permission.repository

import com.michibaum.lifemanagementbackend.permission.domain.Permission
import com.michibaum.lifemanagementbackend.permission.domain.PermissionName
import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository

interface PermissionRepository :
    CustomJpaRepository<Permission, Long> {
    fun findByName(permissionName: PermissionName): Permission?
}
