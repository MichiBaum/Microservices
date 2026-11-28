package com.michibaum.gateway_discovery.model

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.HexFormat

class GatewayMetadataException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

data class DecodedGatewayMetadata(
    val hash: String,
    val metadata: GatewayServiceMetadata
)

object GatewayMetadataCodec {

    private val jsonMapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .build()

    fun serialize(metadata: GatewayServiceMetadata): String {
        try {
            return jsonMapper.writeValueAsString(metadata)
        } catch (ex: Exception) {
            throw GatewayMetadataException("Failed to serialize GatewayServiceMetadata for service '${metadata.serviceId}'", ex)
        }
    }

    fun computeHash(serializedJson: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(serializedJson.toByteArray(StandardCharsets.UTF_8))
        return HexFormat.of().formatHex(digest)
    }

    fun computeHash(metadata: GatewayServiceMetadata): String {
        return computeHash(serialize(metadata))
    }

    fun encodeToMap(metadata: GatewayServiceMetadata): Map<String, String> {
        val json = serialize(metadata)
        val hash = computeHash(json)
        return mapOf(
            GatewayMetadataConstants.KEY_SCHEMA_VERSION to metadata.schemaVersion.toString(),
            GatewayMetadataConstants.KEY_HASH to hash,
            GatewayMetadataConstants.KEY_CONFIG to json
        )
    }

    fun deserialize(json: String): GatewayServiceMetadata {
        if (json.isBlank()) {
            throw GatewayMetadataException("Gateway metadata JSON payload must not be blank")
        }
        val metadata = try {
            jsonMapper.readValue(json, GatewayServiceMetadata::class.java)
        } catch (ex: Exception) {
            throw GatewayMetadataException("Failed to deserialize GatewayServiceMetadata JSON", ex)
        }
        validateSchemaVersion(metadata.schemaVersion)
        if (metadata.serviceId.isBlank()) {
            throw GatewayMetadataException("Deserialized GatewayServiceMetadata has blank serviceId")
        }
        return metadata
    }

    fun decodeFromMap(metadataMap: Map<String, String>?): DecodedGatewayMetadata? {
        if (metadataMap.isNullOrEmpty()) {
            return null
        }
        val rawConfig = metadataMap[GatewayMetadataConstants.KEY_CONFIG]?.takeIf { it.isNotBlank() }
            ?: return null

        metadataMap[GatewayMetadataConstants.KEY_SCHEMA_VERSION]
            ?.takeIf { it.isNotBlank() }
            ?.let { rawVersion ->
                val parsedVersion = rawVersion.toIntOrNull()
                    ?: throw GatewayMetadataException("Invalid gateway metadata schema version '$rawVersion'")
                validateSchemaVersion(parsedVersion)
            }

        val metadata = deserialize(rawConfig)
        val hash = metadataMap[GatewayMetadataConstants.KEY_HASH]?.takeIf { it.isNotBlank() }
            ?: computeHash(rawConfig)
        return DecodedGatewayMetadata(hash = hash, metadata = metadata)
    }

    private fun validateSchemaVersion(version: Int) {
        if (version != GatewayMetadataConstants.CURRENT_SCHEMA_VERSION) {
            throw GatewayMetadataException(
                "Unsupported gateway metadata schemaVersion=$version (expected ${GatewayMetadataConstants.CURRENT_SCHEMA_VERSION})"
            )
        }
    }
}
