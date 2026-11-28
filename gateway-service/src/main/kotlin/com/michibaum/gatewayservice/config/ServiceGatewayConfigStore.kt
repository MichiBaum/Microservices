package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.DecodedGatewayMetadata
import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayMetadataException
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import org.apache.commons.logging.LogFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.discovery.event.HeartbeatEvent
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

data class DiscoveredServiceEntry(
    val serviceId: String,
    val hash: String,
    val metadata: GatewayServiceMetadata,
    val liveInstanceCount: Int
)

data class GatewayDiscoverySnapshot(
    val entriesByServiceId: Map<String, DiscoveredServiceEntry> = emptyMap()
) {
    val hashesByServiceId: Map<String, String> = entriesByServiceId.mapValues { it.value.hash }
    val metadataList: List<GatewayServiceMetadata> = entriesByServiceId.values.map { it.metadata }
}

data class GatewayConfigSnapshotChangedEvent(
    val snapshot: GatewayDiscoverySnapshot
)

data class GatewayDiscoverySyncSummary(
    val updated: Boolean,
    val activeServices: List<String>,
    val evictedServices: List<String>
)

@Component
class ServiceGatewayConfigStore(
    private val discoveryClient: DiscoveryClient?,
    private val eventPublisher: ApplicationEventPublisher? = null
) {

    private val log = LogFactory.getLog(ServiceGatewayConfigStore::class.java)
    private val refreshLock = Any()
    private val snapshotRef = AtomicReference(GatewayDiscoverySnapshot())
    private val listeners = CopyOnWriteArrayList<(GatewayDiscoverySnapshot) -> Unit>()

    fun currentSnapshot(): GatewayDiscoverySnapshot = snapshotRef.get()

    fun currentServices(): List<GatewayServiceMetadata> = snapshotRef.get().metadataList

    fun registerListener(listener: (GatewayDiscoverySnapshot) -> Unit) {
        listeners.add(listener)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        refresh(evictMissingServices = false)
    }

    @EventListener(HeartbeatEvent::class)
    fun onHeartbeat() {
        refresh(evictMissingServices = false)
    }

    @Scheduled(
        fixedDelayString = "\${gateway.discovery.sync-interval-ms:30000}",
        initialDelayString = "\${gateway.discovery.initial-delay-ms:10000}"
    )
    fun scheduledSafetySync() {
        refresh(evictMissingServices = false)
    }

    fun refresh(evictMissingServices: Boolean = false): GatewayDiscoverySyncSummary {
        synchronized(refreshLock) {
            val previousSnapshot = snapshotRef.get()
            val discoveredEntries = LinkedHashMap<String, DiscoveredServiceEntry>()

            val rawServiceIds = try {
                discoveryClient?.services.orEmpty()
            } catch (ex: Exception) {
                log.warn("Failed to read service list from DiscoveryClient: ${ex.message}", ex)
                emptyList()
            }

            for (rawServiceId in rawServiceIds.sorted()) {
                val instances = try {
                    discoveryClient?.getInstances(rawServiceId).orEmpty()
                } catch (ex: Exception) {
                    log.warn("Failed to read instances for service '$rawServiceId' from DiscoveryClient: ${ex.message}", ex)
                    emptyList()
                }
                if (instances.isEmpty()) {
                    continue
                }

                val selected = selectBestInstanceMetadata(rawServiceId, instances) ?: continue
                val canonicalServiceId = selected.metadata.serviceId.trim()
                discoveredEntries[canonicalServiceId] = DiscoveredServiceEntry(
                    serviceId = canonicalServiceId,
                    hash = selected.hash,
                    metadata = selected.metadata,
                    liveInstanceCount = instances.size
                )
            }

            val evictedServices = mutableListOf<String>()
            val mergedEntries = LinkedHashMap<String, DiscoveredServiceEntry>()
            mergedEntries.putAll(discoveredEntries)

            for ((serviceId, previousEntry) in previousSnapshot.entriesByServiceId) {
                if (mergedEntries.containsKey(serviceId)) {
                    continue
                }
                if (evictMissingServices) {
                    evictedServices.add(serviceId)
                    log.info("Evicted stale last-known-good gateway configuration for offline service '$serviceId'")
                } else {
                    mergedEntries[serviceId] = previousEntry.copy(liveInstanceCount = 0)
                    log.debug("Retaining last-known-good gateway configuration for offline service '$serviceId'")
                }
            }

            val sortedEntries = mergedEntries.entries
                .sortedBy { it.key }
                .associate { it.key to it.value }

            val newSnapshot = GatewayDiscoverySnapshot(sortedEntries)
            val hashesChanged = newSnapshot.hashesByServiceId != previousSnapshot.hashesByServiceId

            if (hashesChanged) {
                snapshotRef.set(newSnapshot)
                log.info(
                    "Updated gateway discovery snapshot: services=${newSnapshot.entriesByServiceId.keys}, " +
                        "evicted=$evictedServices"
                )
                notifyListeners(newSnapshot)
            } else if (newSnapshot != previousSnapshot) {
                // Only liveInstanceCount changed; update snapshot reference without rebuilding routes/CORS
                snapshotRef.set(newSnapshot)
            }

            return GatewayDiscoverySyncSummary(
                updated = hashesChanged,
                activeServices = newSnapshot.entriesByServiceId.keys.toList(),
                evictedServices = evictedServices.sorted()
            )
        }
    }

    private fun selectBestInstanceMetadata(
        rawServiceId: String,
        instances: List<ServiceInstance>
    ): DecodedGatewayMetadata? {
        val candidates = instances.mapNotNull { instance ->
            try {
                val decoded = GatewayMetadataCodec.decodeFromMap(instance.metadata)
                if (decoded == null) {
                    null
                } else {
                    InstanceMetadataCandidate(
                        instance = instance,
                        decoded = decoded,
                        lastUpdatedTimestamp = extractLastUpdatedTimestamp(instance)
                    )
                }
            } catch (ex: GatewayMetadataException) {
                log.error(
                    "Ignoring malformed or unsupported gateway metadata from service '$rawServiceId' " +
                        "(instanceId=${instance.instanceId ?: instance.uri}): ${ex.message}"
                )
                null
            } catch (ex: Exception) {
                log.error(
                    "Unexpected error decoding gateway metadata from service '$rawServiceId' " +
                        "(instanceId=${instance.instanceId ?: instance.uri}): ${ex.message}",
                    ex
                )
                null
            }
        }

        if (candidates.isEmpty()) {
            return null
        }

        val distinctHashes = candidates.map { it.decoded.hash }.distinct()
        if (distinctHashes.size > 1) {
            log.info(
                "Detected ${distinctHashes.size} distinct gateway metadata versions across replicas of '$rawServiceId'; " +
                    "selecting most recently updated instance"
            )
        }

        return candidates.sortedWith(
            compareByDescending<InstanceMetadataCandidate> { it.lastUpdatedTimestamp }
                .thenBy { it.instance.instanceId ?: "${it.instance.host}:${it.instance.port}" }
        ).first().decoded
    }

    private fun extractLastUpdatedTimestamp(instance: ServiceInstance): Long {
        if (instance is EurekaServiceInstance) {
            return instance.instanceInfo.lastUpdatedTimestamp
        }
        return instance.metadata?.get("lastUpdatedTimestamp")?.toLongOrNull() ?: 0L
    }

    private fun notifyListeners(snapshot: GatewayDiscoverySnapshot) {
        listeners.forEach { listener ->
            try {
                listener(snapshot)
            } catch (ex: Exception) {
                log.error("Failed to notify gateway discovery snapshot listener: ${ex.message}", ex)
            }
        }
        eventPublisher?.publishEvent(GatewayConfigSnapshotChangedEvent(snapshot))
    }

    private data class InstanceMetadataCandidate(
        val instance: ServiceInstance,
        val decoded: DecodedGatewayMetadata,
        val lastUpdatedTimestamp: Long
    )
}
