package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.Player
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlayerRepository: JpaRepository<Player, UUID>