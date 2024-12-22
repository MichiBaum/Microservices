import {Component, OnChanges, SimpleChanges, input, output, inject, computed} from '@angular/core';
import {ChessEvent, ChessGame} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/services/chess.service";
import {rxResource} from "@angular/core/rxjs-interop";
import { of } from 'rxjs';

@Component({
  selector: 'app-chess-event-games',
  imports: [],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.scss'
})
export class ChessEventGamesComponent {
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  games = rxResource({
    request: () => ({event: this.event()}),
    loader: (params) => {
      let event = this.event();
      if(event == undefined || event.id == undefined)
        return of([])
      return this.chessService.eventGames(event.id)
    }
  })
  computedHaveContent = computed(() => {
    let games = this.games.value();
    if (games == undefined) {
      this.haveContent.emit(false)
      return false
    }
    let haveGames = games.length > 0;
    this.haveContent.emit(haveGames)
    return true
  })
  readonly haveContent = output<boolean>();

}
