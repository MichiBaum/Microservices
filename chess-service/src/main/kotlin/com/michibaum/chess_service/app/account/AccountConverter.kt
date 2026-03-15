package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.stereotype.Component

@Component
class AccountConverter {

    fun convert(account: Account): AccountDto {
        return AccountDto(
            id = account.idOrThrow(),
            name = account.name,
            username = account.username,
            platform = account.platform,
            url = url(account),
            personName = account.person?.fullName()
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
