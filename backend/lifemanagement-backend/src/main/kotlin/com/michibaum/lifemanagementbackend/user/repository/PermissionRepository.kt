package com.michibaum.lifemanagementbackend.user.repository

import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository
import com.michibaum.lifemanagementbackend.user.domain.Permission
import com.michibaum.lifemanagementbackend.user.domain.PermissionName

interface PermissionRepository : CustomJpaRepository<Permission, Long> {
    fun findByName(permissionName: PermissionName): Permission?
}
