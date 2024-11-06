package com.michibaum.fitness_service.api.weight

import org.springframework.stereotype.Service

@Service
class WeightService(
    val weightRepository: WeightRepository
) {

    fun getByUser(userId: String): List<Weight> =
        weightRepository.findAllByUserId(userId)

    fun update(weight: List<Weight>) =
        weightRepository.saveAll(weight)
}