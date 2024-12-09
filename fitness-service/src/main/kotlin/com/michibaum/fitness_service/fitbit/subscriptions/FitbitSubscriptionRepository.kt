package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FitbitSubscriptionRepository: JpaRepository<FitbitSubscription, UUID>