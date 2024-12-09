package com.michibaum.chess_service.app.player

import com.michibaum.chess_service.domain.Player
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlayerRepository: JpaRepository<Player, UUID>