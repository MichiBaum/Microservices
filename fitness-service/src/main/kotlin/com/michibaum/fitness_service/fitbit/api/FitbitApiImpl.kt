package com.michibaum.fitness_service.fitbit.api

import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class FitbitApiImpl: FitbitApi {

    val client = WebClient.builder()
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
            .onStatus({ t -> t.value() == 400 }, { Mono.error(Exception()) }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .bodyToMono(ProfileDto::class.java)
            // TODO .onError
            .block()

    }


    override fun weightLog(credentials: FitbitOAuthCredentials, startDate: String, endDate: String): List<WeightDto> {
        // Maximum date range: 31 days

        return client.get()
            .uri("/1/user/${credentials.fitbitUserId}/body/log/weight/date/$startDate/$endDate.json")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 400 }, { Mono.error(Exception()) }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .bodyToMono(WeightLogDto::class.java)
            // TODO .onError
            .block()
            ?.weight ?: emptyList()
    }

    override fun sleepLog(credentials: FitbitOAuthCredentials): List<SleepDto> {
        // Maximum range: 100 days
        val startDate = "2024-10-01" // Format yyyy-MM-dd
        val endDate= "2024-10-31"

        return client.get()
            .uri("/1.2/user/${credentials.fitbitUserId}/sleep/date/$startDate/$endDate.json")
            .headers {
                it.setBearerAuth(credentials.accessToken)
            }
            .retrieve()
            .onStatus({ t -> t.value() == 400 }, { Mono.error(Exception()) }) // TODO The request had bad syntax or was inherently impossible to be satisfied.
            .onStatus({ t -> t.value() == 401 }, { Mono.error(Exception()) }) // TODO The request requires user authentication. Use FitbitOAuth to refresh token
            .onStatus({ t -> t.value() == 403 }, { Mono.error(Exception()) }) // TODO Forbidden
            .onStatus({ t -> t.value() == 429 }, { Mono.error(Exception()) }) // TODO Returned if the application has reached the rate limit for a specific user. The rate limit will be reset at the top of the hour.
            .bodyToMono(SleepLogDto::class.java)
            // TODO .onError
            .block()
            ?.sleep ?: emptyList()
    }
}