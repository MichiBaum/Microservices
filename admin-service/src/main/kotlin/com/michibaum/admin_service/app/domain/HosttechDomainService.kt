package com.michibaum.admin_service.app.domain

import com.michibaum.admin_service.app.domain.api.*
import com.michibaum.admin_service.app.domain.model.ApiUserV1ZonesGet200Response
import com.michibaum.admin_service.app.domain.model.TXTRecord
import com.michibaum.admin_service.app.domain.model.ZoneWithoutRecords
import org.springframework.stereotype.Service

@Service
class HosttechDomainService(
    val zonesApi: ZonesApi,
    val recordsApi: RecordsApi,
    val tokensApi: TokensApi,
    val toolsApi: ToolsApi,
    val usersApi: UsersApi,
    val nameserversetsApi: NameserversetsApi
){
    
    fun getAllZones(token: String): List<ZoneWithoutRecords> {
        return HosttechAuthContext.withToken(token) {
            zonesApi.apiUserV1ZonesGet("*", 100, 0).data.orEmpty()
        }
    }

    fun removeAllAcmeChallengeRecords(token: String) {
        HosttechAuthContext.withToken(token) {
            val zones = getAllZones(token)

            zones.forEach { zone ->
                val zoneId = zone.id?.toString() ?: return@forEach
                val records = recordsApi.apiUserV1ZonesZoneIdRecordsGet(zoneId, "TXT")

                records.data.orEmpty().forEach { record ->
                    val txtRecord = record.actualInstance
                    if (txtRecord is TXTRecord && txtRecord.name == "_acme-challenge") {
                        recordsApi.apiUserV1ZonesZoneIdRecordsRecordIdDelete(zoneId, txtRecord.id)
                    }
                }
            }
        }
    }
    
}
