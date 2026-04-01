package com.michibaum.chess_service.database

enum class TerminationType {
    CHECKMATE,
    RESIGNATION,
    TIMEOUT,
    STALEMATE,
    DRAW_AGREEMENT,
    REPETITION,
    FIFTY_MOVE_RULE,
    INSUFFICIENT_MATERIAL,
    ABANDONMENT,
    TIME_EXPIRED_VS_INSUFFICIENT_MATERIAL
}
