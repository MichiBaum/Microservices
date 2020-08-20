package com.michibaum.lifemanagementbackend.user.repository

import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository
import com.michibaum.lifemanagementbackend.user.domain.HttpHeader

interface HttpHeaderRepository : CustomJpaRepository<HttpHeader, Long> {
    fun findByNameAndValue(name: String, value: String): HttpHeader?
}
