package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account

interface AccountSearch {
    
    fun responsibleFor(searchLocation: SearchLocation): Boolean
    
    fun search(searchAccountDto: SearchAccountDto): List<Account>
    
}