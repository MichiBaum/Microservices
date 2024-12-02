package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FitbitSubscriptionRepository: JpaRepository<FitbitSubscription, UUID>