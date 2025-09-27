package com.michibaum.usermanagement_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPermissionMappingRepository : JpaRepository<UserPermissionMapping, UserPermissionMappingId> 