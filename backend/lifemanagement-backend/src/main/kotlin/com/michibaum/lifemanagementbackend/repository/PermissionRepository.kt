package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.domain.PermissionName

interface PermissionRepository :
    CustomJpaRepository<Permission, Long> {
    fun findByName(permissionName: PermissionName): Permission?
}
