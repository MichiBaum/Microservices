import {Component, inject, input, output, effect, computed, ChangeDetectionStrategy} from '@angular/core';
import {ChessEvent, GameResult} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/api-services/chess.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from 'rxjs';
import {TranslateModule} from "@ngx-translate/core";
import {CardModule} from "primeng/card";
import {DatePipe} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCalendarAlt, faStopwatch} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-chess-event-games',
  imports: [
    TranslateModule,
    CardModule,
    DatePipe,
    FaIconComponent
  ],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChessEventGamesComponent {
  protected readonly faCalendarAlt = faCalendarAlt;
  protected readonly faStopwatch = faStopwatch;
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
    },
    defaultValue: []
  })

  sortedGames = computed(() => {
    const games = this.games.value() ?? [];
    return [...games].sort((a, b) => new Date(b.playedAt).getTime() - new Date(a.playedAt).getTime());
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


