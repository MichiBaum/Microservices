package com.michibaum.music_service.apis.spotify.oauth

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.music_service.config.properties.ApisProperties
import com.michibaum.music_service.config.properties.SpotifyOAuthProperties
import com.michibaum.music_service.database.spotify.SpotifyAccountActivatedRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentials
import com.michibaum.music_service.database.spotify.SpotifyOAuthCredentialsRepository
import com.michibaum.music_service.database.spotify.SpotifyOAuthData
import com.michibaum.music_service.database.spotify.SpotifyOAuthDataRepository
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class SpotifyOAuthService(
    private val spotifyOAuthDataRepository: SpotifyOAuthDataRepository,
    private val spotifyOAuthCredentialsRepository: SpotifyOAuthCredentialsRepository,
    private val spotifyAccountActivatedRepository: SpotifyAccountActivatedRepository,
    private val spotifyOAuthProperties: SpotifyOAuthProperties,
    private val apisProperties: ApisProperties,
    restClientBuilder: RestClient.Builder,
    private val observationRegistry: ObservationRegistry? = null,
) {

    private val logger = LoggerFactory.getLogger(SpotifyOAuthService::class.java)

    private val client: RestClient
    private val userLocks = ConcurrentHashMap<String, Any>()

    init {
        val clientAndSecret = spotifyOAuthProperties.clientId + ":" + spotifyOAuthProperties.clientSecret
        val authBasic = Base64.getEncoder().encodeToString(clientAndSecret.encodeToByteArray())

        client = restClientBuilder
            .baseUrl("https://accounts.spotify.com")
            .defaultHeaders {
                it.set(HttpHeaders.AUTHORIZATION, "Basic $authBasic")
                it.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                it.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                it.set(HttpHeaders.USER_AGENT, apisProperties.userAgent)
            }
            .build()
    }

    @Observed(name = "spotify.oauth.generate.data")
    @Transactional
    fun generateData(principal: JwtAuthentication): SpotifyOAuthData {
        val state = generateState()

        val spotifyOAuthData = SpotifyOAuthData(
            state = state,
            userId = principal.getUserId()
        )
        return spotifyOAuthDataRepository.save(spotifyOAuthData)
    }

    private fun generateState(): String {
        val secureRandom = SecureRandom()
        val state = ByteArray(16)
        secureRandom.nextBytes(state)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(state)
    }

    @Observed(name = "spotify.oauth.find.by.state")
    @Transactional(readOnly = true)
    fun findByState(state: String): SpotifyOAuthData? =
        spotifyOAuthDataRepository.findByState(state)

    @Observed(name = "spotify.oauth.save")
    @Transactional
    fun save(response: SpotifyOAuthCredentialsDto, oAuthData: SpotifyOAuthData): SpotifyOAuthCredentials =
        saveCredentials(response, oAuthData.userId)

    @Observed(name = "spotify.oauth.save.credentials")
    @Transactional
    fun saveCredentials(response: SpotifyOAuthCredentialsDto, userId: String): SpotifyOAuthCredentials {
        val activeCredentials = spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId)
        val existingByRefreshToken = if (response.refreshToken.isNotBlank()) {
            spotifyOAuthCredentialsRepository.findByUserIdAndRefreshToken(userId, response.refreshToken)
        } else null

        val targetCredentials = existingByRefreshToken ?: activeCredentials.maxByOrNull { it.createdDate }

        // Deactivate all active credentials for this user except targetCredentials
        activeCredentials.filter { it.id != targetCredentials?.id }.forEach {
            it.deactivated = true
            spotifyOAuthCredentialsRepository.save(it)
        }

        return if (targetCredentials != null) {
            targetCredentials.accessToken = response.accessToken
            targetCredentials.expiresIn = response.expiresIn.toInt()
            if (response.refreshToken.isNotBlank()) {
                targetCredentials.refreshToken = response.refreshToken
            }
            if (response.scope.isNotBlank()) {
                targetCredentials.scope = response.scope
            }
            targetCredentials.createdDate = Instant.now()
            targetCredentials.validUntil = Instant.now().plusSeconds(response.expiresIn.toLong())
            targetCredentials.deactivated = false
            spotifyOAuthCredentialsRepository.save(targetCredentials)
        } else {
            val effectiveRefreshToken = if (response.refreshToken.isNotBlank()) {
                response.refreshToken
            } else {
                spotifyOAuthCredentialsRepository.findByUserId(userId).maxByOrNull { it.createdDate }?.refreshToken ?: ""
            }
            val credentials = SpotifyOAuthCredentials(
                accessToken = response.accessToken,
                expiresIn = response.expiresIn.toInt(),
                refreshToken = effectiveRefreshToken,
                scope = response.scope,
                userId = userId,
                createdDate = Instant.now(),
                validUntil = Instant.now().plusSeconds(response.expiresIn.toLong()),
                deactivated = false,
            )
            spotifyOAuthCredentialsRepository.save(credentials)
        }
    }

    @Observed(name = "spotify.oauth.active.credentials")
    @Transactional
    fun activeCredentialsByUser(userId: String): SpotifyOAuthCredentials? {
        val activeList = spotifyOAuthCredentialsRepository.findAllByUserIdAndDeactivatedFalse(userId)
        if (activeList.isEmpty()) {
            return null
        }
        if (activeList.size == 1) {
            return activeList.first()
        }

        // Self-healing: if multiple active credentials exist in DB for a single user,
        // keep the newest active one and deactivate the older duplicate entries.
        val newest = activeList.maxBy { it.createdDate }
        activeList.filter { it.id != newest.id }.forEach { older ->
            older.deactivated = true
            spotifyOAuthCredentialsRepository.save(older)
        }
        return newest
    }

    @Observed(name = "spotify.oauth.get.active.token")
    fun getActiveToken(userId: String, force: Boolean = false): SpotifyOAuthCredentials? {
        val lock = userLocks.computeIfAbsent(userId) { Any() }
        return synchronized(lock) {
            val credentials = activeCredentialsByUser(userId) ?: return null
            if (!force) {
                val nowWithBuffer = Instant.now().plusSeconds(300)
                if (!nowWithBuffer.isAfter(credentials.validUntil)) {
                    return credentials
                }
            }

            if (credentials.refreshToken.isBlank()) {
                logger.warn("Cannot refresh Spotify token for user $userId because refresh token is blank.")
                return null
            }

            val bodyMap = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "refresh_token")
                add("refresh_token", credentials.refreshToken)
            }

            val observation = observationRegistry?.let { registry ->
                Observation.createNotStarted("spotify.oauth.token.refresh", registry)
                    .highCardinalityKeyValue("spotify.user_id", userId)
                    .lowCardinalityKeyValue("spotify.force_refresh", force.toString())
                    .start()
            }

            try {
                val response = client
                    .post()
                    .uri("/api/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(bodyMap)
                    .retrieve()
                    .toEntity(SpotifyOAuthCredentialsDto::class.java)

                val body = response.body
                if (response.statusCode.is2xxSuccessful && body != null) {
                    observation?.lowCardinalityKeyValue("spotify.refresh_status", "success")
                    saveCredentials(body, userId)
                } else {
                    observation?.lowCardinalityKeyValue("spotify.refresh_status", "non_2xx")
                    logger.warn("Spotify token refresh returned non-2xx status code: ${response.statusCode}")
                    null
                }
            } catch (e: Exception) {
                observation?.lowCardinalityKeyValue("spotify.refresh_status", "error")
                observation?.error(e)
                logger.error("Failed to refresh Spotify token for user $userId", e)
                null
            } finally {
                observation?.stop()
            }
        }
    }

    @Observed(name = "spotify.oauth.is.enabled")
    @Transactional(readOnly = true)
    fun isSpotifyAccountEnabled(userId: String): Boolean =
        spotifyAccountActivatedRepository.existsByUserId(userId)

    @Observed(name = "spotify.oauth.has.valid.token")
    fun hasValidToken(userId: String): Boolean {
        if (userId.isBlank()) return false
        return getActiveToken(userId) != null
    }
}