import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ChessEvent, ChessGame} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/services/chess.service";

@Component({
  selector: 'app-chess-event-games',
  standalone: true,
  imports: [],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.scss'
})
export class ChessEventGamesComponent implements OnChanges {
  @Input() event: ChessEvent | undefined;
  games: ChessGame[] | undefined;

  constructor(
    private readonly chessService: ChessService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['event'] && changes['event'].currentValue) {
      this.chessService.eventGames(changes['event'].currentValue.id).subscribe(games => {
        this.games = [...games]
      })
    }
  }

}
