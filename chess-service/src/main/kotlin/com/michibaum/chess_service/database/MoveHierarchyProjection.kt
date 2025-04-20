package com.michibaum.chess_service.database

import java.util.*

interface MoveHierarchyProjection {
    fun getMoveId(): UUID
    fun getMove(): String
    fun getFen(): String
    fun getParentId(): UUID?
    fun getEngineId(): UUID?
    fun getDepth(): Int?
    fun getEvaluationId(): String?
    fun getEvaluation(): String?
    fun getOpeningName(): String?
    fun getOpeningId(): UUID?
}