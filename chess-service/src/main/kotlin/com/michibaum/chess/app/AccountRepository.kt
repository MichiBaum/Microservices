package com.michibaum.chess.app

import com.michibaum.chess.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository: JpaRepository<Account, UUID> {

}