package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.repository.PurchasebillRepository
import org.springframework.stereotype.Service

@Service
class PurchasebillService(
    private val purchasebillRepository: PurchasebillRepository
) {
}
