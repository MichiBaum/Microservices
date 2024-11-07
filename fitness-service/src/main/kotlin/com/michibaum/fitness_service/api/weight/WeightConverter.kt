package com.michibaum.fitness_service.api.weight

import org.springframework.stereotype.Component

@Component
class WeightConverter {

    fun toDto(weight: Weight) =
        WeightDto(
            weight = weight.weight,
            bmi = weight.bmi,
            fatPercentage = weight.fatPercentage,
            date = weight.date,
            id = weight.id
        )

}