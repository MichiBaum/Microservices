package com.michibaum.usermanagement_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserPermissionMappingRepository : JpaRepository<UserPermissionMapping, UserPermissionMappingId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM UserPermissionMapping upm WHERE upm.user = :user")
    fun deleteByUser(user: User)
}