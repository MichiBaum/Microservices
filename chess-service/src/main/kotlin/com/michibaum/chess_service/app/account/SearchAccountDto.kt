package com.michibaum.chess_service.app.account

data class SearchAccountDto(
    val accountName: String,
    val searchLocation: SearchLocation = SearchLocation.LOCAL
)
