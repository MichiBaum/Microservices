package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.Store

interface StoreRepository : CustomJpaRepository<Store, Long> {
}
