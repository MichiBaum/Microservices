package com.michibaum.fitness_service.fitbit.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileDto(
    val user: UserDto,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(
    val age: Long,
    val autoStrideEnabled: Boolean,
    val avatar: String,
    val avatar150: String,
    val avatar640: String,
    val averageDailySteps: Long,
    val challengesBeta: Boolean,
    val clockTimeDisplayFormat: String,
    val country: String,
    val dateOfBirth: String,
    val displayName: String,
    val displayNameSetting: String,
    val distanceUnit: String,
    val encodedId: String,
    val features: FeaturesDto,
    val firstName: String,
    val foodsLocale: String,
    val fullName: String,
    val gender: String,
    val glucoseUnit: String,
    val height: Double,
    val heightUnit: String,
    val languageLocale: String,
    val lastName: String,
    val legalTermsAcceptRequired: Boolean,
    val locale: String,
    val memberSince: String,
    val mfaEnabled: Boolean,
    @JsonProperty("offsetFromUTCMillis")
    val offsetFromUtcmillis: Long,
    val sdkDeveloper: Boolean,
    val sleepTracking: String,
    val startDayOfWeek: String,
    val strideLengthRunning: Double,
    val strideLengthRunningType: String,
    val strideLengthWalking: Double,
    val strideLengthWalkingType: String,
    val swimUnit: String,
    val temperatureUnit: String,
    val timezone: String,
    val topBadges: List<TopBadgeDto>,
    val visibleUser: Boolean,
    val waterUnit: String,
    val waterUnitName: String,
    val weight: Double,
    val weightUnit: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeaturesDto(
    val exerciseGoal: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TopBadgeDto(
    val badgeGradientEndColor: String,
    val badgeGradientStartColor: String,
    val badgeType: String,
    val category: String,
    val cheers: List<Any?>,
    val dateTime: String,
    val description: String,
    val earnedMessage: String,
    val encodedId: String,
    val image100px: String,
    val image125px: String,
    val image300px: String,
    val image50px: String,
    val image75px: String,
    val marketingDescription: String,
    val mobileDescription: String,
    val name: String,
    val shareImage640px: String,
    val shareText: String,
    val shortDescription: String,
    val shortName: String,
    val timesAchieved: Long,
    val value: Long,
    val unit: String?,
)