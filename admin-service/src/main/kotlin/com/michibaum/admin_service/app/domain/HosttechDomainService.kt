package com.michibaum.admin_service.app.domain

import com.michibaum.admin_service.app.domain.api.*
import org.springframework.stereotype.Service

@Service
class HosttechDomainService(
    val zonesApi: ZonesApi,
    val recordsApi: RecordsApi,
    val tokensApi: TokensApi,
    val toolsApi: ToolsApi,
    val usersApi: UsersApi,
    val nameserversetsApi: NameserversetsApi
)
