package com.michibaum.chess_service.app.opening

import java.util.*

interface MoveHierarchyProjection {
    fun getMoveId(): UUID
    fun getMove(): String
    fun getParentId(): UUID?
    fun getEngineId(): UUID?
    fun getDepth(): Int?
    fun getEvaluation(): String?
}