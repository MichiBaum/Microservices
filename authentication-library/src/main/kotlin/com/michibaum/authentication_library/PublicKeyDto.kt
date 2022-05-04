package com.michibaum.authentication_library

import lombok.AllArgsConstructor
import lombok.Data

@Data
@AllArgsConstructor
class PublicKeyDto (
    val algorithm: String,
    val key: ByteArray
)