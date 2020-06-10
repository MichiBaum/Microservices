package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.domain.Permission
import com.michibaum.lifemanagementbackend.repository.PermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    fun findById(id: Long): Permission? =
        permissionRepository.findById(id).orElseGet(null)

    fun findAll(): List<Permission> =
        permissionRepository.findAll()

}
