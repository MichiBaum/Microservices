package com.michibaum.admin_service.app.domain

import com.michibaum.admin_service.app.domain.api.*
import com.michibaum.admin_service.app.domain.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HosttechDomainServiceTest {

    private val zonesApi = mockk<ZonesApi>()
    private val recordsApi = mockk<RecordsApi>()
    private val tokensApi = mockk<TokensApi>()
    private val toolsApi = mockk<ToolsApi>()
    private val usersApi = mockk<UsersApi>()
    private val nameserversetsApi = mockk<NameserversetsApi>()

    private val service = HosttechDomainService(
        zonesApi, recordsApi, tokensApi, toolsApi, usersApi, nameserversetsApi
    )

    @Test
    fun `getAllZones calls zonesApi with correct parameters`() {
        val token = "test-token"
        val expectedZone = ZoneWithoutRecords(1)
        val apiResponse = ApiUserV1ZonesGet200Response().apply {
            setData(listOf(expectedZone))
        }

        every { zonesApi.apiUserV1ZonesGet("*", 100, 0) } returns apiResponse

        val result = service.getAllZones(token)

        assertEquals(1, result.size)
        assertEquals(expectedZone, result[0])
        verify(exactly = 1) { zonesApi.apiUserV1ZonesGet("*", 100, 0) }
    }

    @Test
    fun `removeAllAcmeChallengeRecords removes TXT records with _acme-challenge name`() {
        val token = "test-token"
        val zoneId = 123
        val recordId = 456

        val zone = ZoneWithoutRecords(zoneId)
        val zonesResponse = ApiUserV1ZonesGet200Response().apply {
            setData(listOf(zone))
        }

        val txtRecord = TXTRecord(recordId, "TXT").apply {
            setName("_acme-challenge")
        }
        val record = Record(txtRecord)
        
        val recordsResponse = ApiUserV1ZonesZoneIdRecordsGet200Response().apply {
            setData(listOf(record))
        }

        every { zonesApi.apiUserV1ZonesGet("*", 100, 0) } returns zonesResponse
        every { recordsApi.apiUserV1ZonesZoneIdRecordsGet(zoneId.toString(), "TXT") } returns recordsResponse
        every { recordsApi.apiUserV1ZonesZoneIdRecordsRecordIdDelete(zoneId.toString(), recordId) } returns Unit

        service.removeAllAcmeChallengeRecords(token)

        verify(exactly = 1) { 
            recordsApi.apiUserV1ZonesZoneIdRecordsRecordIdDelete(zoneId.toString(), recordId) 
        }
    }

    @Test
    fun `removeAllAcmeChallengeRecords ignores non _acme-challenge records`() {
        val token = "test-token"
        val zoneId = 123

        val zone = ZoneWithoutRecords(zoneId)
        val zonesResponse = ApiUserV1ZonesGet200Response().apply {
            setData(listOf(zone))
        }

        val txtRecord = TXTRecord(456, "TXT").apply {
            setName("other")
        }
        val record = Record(txtRecord)
        
        val recordsResponse = ApiUserV1ZonesZoneIdRecordsGet200Response().apply {
            setData(listOf(record))
        }

        every { zonesApi.apiUserV1ZonesGet("*", 100, 0) } returns zonesResponse
        every { recordsApi.apiUserV1ZonesZoneIdRecordsGet(zoneId.toString(), "TXT") } returns recordsResponse

        service.removeAllAcmeChallengeRecords(token)

        verify(exactly = 0) { 
            recordsApi.apiUserV1ZonesZoneIdRecordsRecordIdDelete(any(), any()) 
        }
    }
}
