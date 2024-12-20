import { Component, OnChanges, SimpleChanges, input, output, inject } from '@angular/core';
import {ChessEvent, ChessGame} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/services/chess.service";

@Component({
  selector: 'app-chess-event-games',
  imports: [],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.scss'
})
export class ChessEventGamesComponent implements OnChanges {
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  games: ChessGame[] | undefined;
  readonly haveContent = output<boolean>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['event'] && changes['event'].currentValue) {
      this.chessService.eventGames(changes['event'].currentValue.id).subscribe(games => {
        this.games = [...games]
        this.haveContent.emit(this.games.length > 0)
      })
    }
  }

}
