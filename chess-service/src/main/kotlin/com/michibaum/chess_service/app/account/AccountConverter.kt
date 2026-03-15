package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.stereotype.Component

@Component
class AccountConverter {

    fun convert(account: Account): GetAccountDto {
        return GetAccountDto(
            id = account.id,
            platformId = account.platformId,
            name = account.name,
            username = account.username,
            platform = account.platform,
            url = url(account),
            person = account.person?.let { GetAccountPersonDto(it.id!!, it.fullName()) },
            createdAt = account.createdAt?.toString()
        )
    }
    fun url(account: Account): String {
        return when (account.platform) {
            ChessPlatform.CHESSCOM -> "https://www.chess.com/member/${account.username}"
            ChessPlatform.LICHESS -> "https://lichess.org/@/${account.platformId}"
            ChessPlatform.FIDE -> "https://ratings.fide.com/profile/${account.platformId}"
            ChessPlatform.FREESTYLE -> "https://www.freestyle-chess-players-club.com/"
        }
    }

}
