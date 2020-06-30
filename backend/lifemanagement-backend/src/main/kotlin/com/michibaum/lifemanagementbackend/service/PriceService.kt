package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.repository.PriceRepository
import org.springframework.stereotype.Service

@Service
class PriceService(
    private val priceRepository: PriceRepository
) {
}
