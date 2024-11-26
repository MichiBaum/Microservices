package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class FitbitSupscriptionService(
    private val fitbitSubscriptionRepository: FitbitSubscriptionRepository
) {

    fun findById(id: String) =
        fitbitSubscriptionRepository.findById(UUID.fromString(id)).getOrNull()

}
