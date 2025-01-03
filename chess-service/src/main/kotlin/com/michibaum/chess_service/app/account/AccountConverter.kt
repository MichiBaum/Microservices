package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import org.springframework.stereotype.Component

@Component
class AccountConverter {

    fun convert(account: Account): AccountDto {
        return AccountDto(
            id = account.idOrThrow(),
            username = account.username,
            platform = account.platform,
            url = url(account),
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
