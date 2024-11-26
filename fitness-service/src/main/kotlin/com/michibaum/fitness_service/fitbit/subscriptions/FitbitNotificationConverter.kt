package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.stereotype.Component

@Component
class FitbitNotificationConverter(
    private val fitbitSupscriptionService: FitbitSupscriptionService
) {

    fun toDomain(dto: NotificationDto): FitbitNotification {
        val subscription = lookupSupscription(dto.subscriptionId)

        return FitbitNotification(
            collectionType = convertCollectioType(dto.collectionType),
            date = dto.date,
            ownerId = dto.ownerId,
            ownerType = dto.ownerType,
            subscriptionId = subscription,
            userId = subscription.userId
        )
    }

    private fun lookupSupscription(id: String) =
        fitbitSupscriptionService.findById(id) ?: throw Exception("Could not find subscription with id $id")

    private fun convertCollectioType(collectionType: String): NotificationCollectionType {
        return NotificationCollectionType.entries.first { it.fitbitStrings == collectionType }
    }
        
}