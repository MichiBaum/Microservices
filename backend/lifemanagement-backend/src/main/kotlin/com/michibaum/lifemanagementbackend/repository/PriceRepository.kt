package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.Price

interface PriceRepository : CustomJpaRepository<Price, Long> {
}
