package com.michibaum.fitness_service.fitbit.api

import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials

interface FitbitApi {

    fun profile(credentials: FitbitOAuthCredentials): ProfileDto?

    fun weightLog(credentials: FitbitOAuthCredentials, startDate: String, endDate: String): List<WeightDto>

    fun sleepLog(credentials: FitbitOAuthCredentials): List<SleepDto>

}