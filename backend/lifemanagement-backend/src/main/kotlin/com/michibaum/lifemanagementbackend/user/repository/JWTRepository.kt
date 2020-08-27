package com.michibaum.lifemanagementbackend.user.repository

import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository
import com.michibaum.lifemanagementbackend.user.domain.JWT

interface JWTRepository : CustomJpaRepository<JWT, Long> {
    fun findByJwt(token: String): JWT?
    fun findByActive(active: Boolean): MutableList<JWT>
}
