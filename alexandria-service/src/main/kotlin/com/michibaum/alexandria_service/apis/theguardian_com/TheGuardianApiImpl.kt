package com.michibaum.alexandria_service.apis.theguardian_com

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.LocalDate

@Service
class TheGuardianApiImpl(
    restClientBuilder: RestClient.Builder,
): TheGuardianApi {

    val restClient = restClientBuilder.baseUrl("https://content.guardianapis.com")
    .defaultHeader("User-Agent", "news-application/0.1; +https://michibaum.ch")
    .build()

    override fun getTags() {
        TODO("Not yet implemented")
    }

    // https://open-platform.theguardian.com/documentation/search
    override fun search(query: String, tags: List<String>, from: LocalDate, to: LocalDate, page: Int) {
        restClient.get()
            .uri("/search")
            .attributes {
                it["query"] = query
                it["tags"] = tags // TODO
                it["lang"] = "en,de"
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
    }


}