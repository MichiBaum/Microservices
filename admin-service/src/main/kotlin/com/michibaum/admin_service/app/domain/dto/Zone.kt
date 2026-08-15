package com.michibaum.admin_service.app.domain.dto

import com.michibaum.admin_service.app.domain.model.ZoneWithoutRecords

class ZoneDto (
    val id: Int?,
    val name: String,
    val email: String,
    val ttl: Int,
    val nameserver: String,
    val dnssec: Boolean,
    val dnssecEmail: String?
) {
}

fun ZoneWithoutRecords.toDto(): ZoneDto = ZoneDto (
    id = id,
    name = name ?: "",
    email = email ?: "",
    ttl = ttl ?: 0,
    nameserver = nameserver ?: "",
    dnssec = dnssec ?: false,
    dnssecEmail = dnssecEmail
)