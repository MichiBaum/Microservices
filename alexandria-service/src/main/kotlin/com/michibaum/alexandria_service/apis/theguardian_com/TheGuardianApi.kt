package com.michibaum.alexandria_service.apis.theguardian_com

import java.time.LocalDate

/**
 * https://open-platform.theguardian.com/
 */
interface TheGuardianApi {

    fun getTags()
    fun search(query: String, tags: List<String> = emptyList(), from: LocalDate, to: LocalDate, page: Int = 1)

}