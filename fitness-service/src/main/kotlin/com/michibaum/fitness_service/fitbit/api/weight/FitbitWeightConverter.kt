package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FitbitWeightConverter {

    fun toDomain(weightDto: WeightDto, userId: String) =
        FitbitWeight(
            weight = weightDto.weight,
            bmi = weightDto.bmi,
            fatPercentage = weightDto.fat,
            date = LocalDateTime.parse("${weightDto.date}T${weightDto.time}"),
            userId = userId,
            fitbitId = weightDto.logId
        )

}