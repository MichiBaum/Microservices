import {Component, inject, input, output, effect} from '@angular/core';
import {ChessEvent, GameResult} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/api-services/chess.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from 'rxjs';
import {TableModule} from "primeng/table";
import {TranslateModule} from "@ngx-translate/core";
import {CardModule} from "primeng/card";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-chess-event-games',
  imports: [
    TableModule,
    TranslateModule,
    CardModule,
    DatePipe
  ],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.css'
})
export class ChessEventGamesComponent {
  protected readonly GameResult = GameResult;
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  games = rxResource({
    params: () => ({eventId: this.event()?.id}),
    stream: (params) => {
      let eventId = params.params.eventId;
      if(eventId == undefined)
        return of([])
      return this.chessService.eventGames(eventId)
    }
  })

  readonly haveContent = output<boolean>();

  constructor() {
    effect(() => {
      let games = this.games.value();
      if (games == undefined) {
        this.haveContent.emit(false);
      } else {
        this.haveContent.emit(games.length > 0);
      }
    });
  }

}


