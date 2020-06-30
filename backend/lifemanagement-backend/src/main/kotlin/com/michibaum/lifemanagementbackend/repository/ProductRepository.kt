package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.Product

interface ProductRepository : CustomJpaRepository<Product, Long> {
}
