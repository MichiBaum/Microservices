package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.Account
import org.springframework.stereotype.Component

@Component
class AccountConverter {

    fun convert(account: Account): AccountDto {
        return AccountDto(
            id = account.idOrThrow(),
            username = account.username,
            platform = account.platform,
            url = account.url,
        )
    }

}
