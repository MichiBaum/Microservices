package com.michibaum.discord.api.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data Transfer Object (DTO) representing a Discord HTTP 429 rate-limit response body.
 *
 * @property message A message saying you are being rate limited.
 * @property retryAfter The number of seconds to wait before submitting another request.
 * @property global Whether you are being globally rate limited across all endpoints.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RateLimitResponseDto(
    @JsonProperty("message") val message: String? = null,
    @JsonProperty("retry_after") val retryAfter: Double? = null,
    @JsonProperty("global") val global: Boolean? = null,
)
