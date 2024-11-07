package com.michibaum.fitness_service.fitbit.api.weight

import com.michibaum.fitness_service.api.weight.Weight
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class FitbitWeightConverter {

    fun toDomain(weightDto: WeightDto, userId: String) =
        Weight(
            weight = weightDto.weight,
            bmi = weightDto.bmi,
            fatPercentage = weightDto.fat,
            date = LocalDateTime.parse("${weightDto.date}T${weightDto.time}"),
            userId = userId,
            id = UUID.randomUUID()
        )

}