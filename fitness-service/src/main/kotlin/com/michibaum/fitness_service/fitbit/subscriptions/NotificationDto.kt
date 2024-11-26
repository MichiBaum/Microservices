package com.michibaum.fitness_service.fitbit.subscriptions

data class NotificationDto(
    val collectionType: String,
    val date: String,
    val ownerId: String,
    val ownerType: String,
    val subscriptionId: String,
)
