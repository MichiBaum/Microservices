package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FitbitSubscriptionRepository: JpaRepository<FitbitSubscription, UUID>