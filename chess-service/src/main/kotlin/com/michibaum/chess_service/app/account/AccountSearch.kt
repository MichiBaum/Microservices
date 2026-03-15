package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account

interface AccountSearch {
    
    fun responsibleFor(local: Boolean): Boolean
    
    fun search(query: String): List<Account>
    
}