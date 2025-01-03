import {Component, inject, input} from '@angular/core';
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {TableModule} from "primeng/table";
import {TranslateModule} from "@ngx-translate/core";
import {Account, ChessEvent, Person} from "../../../core/models/chess/chess.models";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {ChessService} from "../../../core/services/chess.service";
import {ChessPlayerCardComponent} from "../chess-player-card/chess-player-card.component";

@Component({
  selector: 'app-chess-event-participants',
  imports: [
    CardModule,
    NgIf,
    TableModule,
    TranslateModule,
    NgForOf,
    ChessPlayerCardComponent
  ],
  templateUrl: './chess-event-participants.component.html',
  styleUrl: './chess-event-participants.component.scss'
})
export class ChessEventParticipantsComponent {
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  protected readonly participants = rxResource({
    request: () => ({eventId: this.event()?.id}),
    loader: (params) => {
      let eventId = params.request.eventId;
      if(eventId == undefined)
        return of([])
      return this.chessService.eventParticipants(eventId)
    }
  })

  personsPlatformAccount(participant: Person): Account[] {
    const eventPlatform = this.event()?.platform
    if(eventPlatform == undefined)
      return []
    return participant.accounts.filter(value => value.platform == eventPlatform)
  }
}
