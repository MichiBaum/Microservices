package com.michibaum.lifemanagementbackend.user.repository

import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository
import com.michibaum.lifemanagementbackend.user.domain.LoginLog

interface LoginLogRepository : CustomJpaRepository<LoginLog, Long> {
}
