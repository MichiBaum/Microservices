package com.michibaum.fitness_service.fitbit.subscriptions

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class FitbitNotification(
    @Enumerated(EnumType.STRING)
    val collectionType: NotificationCollectionType,

    @Column(nullable = false, unique = false)
    val date: String,

    @Column(nullable = false, unique = false)
    val ownerId: String,

    @Column(nullable = false, unique = false)
    val ownerType: String,

    @ManyToOne(targetEntity = FitbitSubscription::class, fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name="subscription_id", nullable=false)
    val subscriptionId: FitbitSubscription,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
)