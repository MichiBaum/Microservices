package com.michibaum.fitness_service.fitbit.api

import com.michibaum.fitness_service.fitbit.api.profile.ProfileDto
import com.michibaum.fitness_service.fitbit.api.sleep.SleepDto
import com.michibaum.fitness_service.fitbit.api.sleep.SleepLogDto
import com.michibaum.fitness_service.fitbit.api.weight.WeightDto
import com.michibaum.fitness_service.fitbit.api.weight.WeightLogDto
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class FitbitApiImpl: FitbitApi {

    val client = RestClient.builder()
    .baseUrl("https://api.fitbit.com")
    .defaultHeaders {
        it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    }
    .build()

    override fun profile(credentials: FitbitOAuthCredentials): ProfileDto? {
        return client.get()
            .uri("/1/user/${credentials.fitbitUserId}/profile.json")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 400 }, { _, _ ->  }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { _, _ ->  }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { _, _ ->  }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { _, _ ->  }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .body<ProfileDto>()
    }


    override fun weightLog(credentials: FitbitOAuthCredentials, startDate: String, endDate: String): List<WeightDto> {
        // Maximum date range: 31 days

        return client.get()
            .uri("/1/user/${credentials.fitbitUserId}/body/log/weight/date/$startDate/$endDate.json")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 400 }, { _, _ ->  }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { _, _ ->  }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { _, _ ->  }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { _, _ ->  }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .body<WeightLogDto>()
            ?.weight ?: emptyList()
    }

    override fun sleepLog(credentials: FitbitOAuthCredentials, startDate: String, endDate: String): List<SleepDto> {
        // Maximum range: 100 days

        return client.get()
            .uri("/1.2/user/${credentials.fitbitUserId}/sleep/date/$startDate/$endDate.json")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 400 }, { _, _ ->  }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { _, _ ->  }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { _, _ ->  }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { _, _ ->  }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .body<SleepLogDto>()
            ?.sleep ?: emptyList()
    }
}