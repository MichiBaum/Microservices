package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
}
