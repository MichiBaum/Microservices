import {Component, computed, inject, input, output} from '@angular/core';
import {ChessEvent} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/api-services/chess.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from 'rxjs';

@Component({
  selector: 'app-chess-event-games',
  imports: [],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.css'
})
export class ChessEventGamesComponent {
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  games = rxResource({
    request: () => ({eventId: this.event()?.id}),
    loader: (params) => {
      let eventId = params.request.eventId;
      if(eventId == undefined)
        return of([])
      return this.chessService.eventGames(eventId)
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


