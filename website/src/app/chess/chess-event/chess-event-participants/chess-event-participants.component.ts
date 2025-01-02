import {Component, computed, inject, input} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {NgIf} from "@angular/common";
import {TableModule} from "primeng/table";
import {RouterNavigationService} from "../../../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {Account, ChessEvent, ChessPlatform, Person} from "../../../core/models/chess/chess.models";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {ChessService} from "../../../core/services/chess.service";

@Component({
  selector: 'app-chess-event-participants',
  imports: [
    Button,
    CardModule,
    NgIf,
    TableModule,
    TranslateModule
  ],
  templateUrl: './chess-event-participants.component.html',
  styleUrl: './chess-event-participants.component.scss'
})
export class ChessEventParticipantsComponent {
  private readonly navigationService = inject(RouterNavigationService);

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

  constructor(private readonly chessService: ChessService) {
  }

  openAccount(account: Account) {
    this.navigationService.open(account.url)
  }

  personsPlatformAccount(participant: Person) {
    const eventPlatform = this.event()?.platform
    if(eventPlatform == undefined)
      return undefined
    const accounts = participant.accounts.filter(value => value.platform == eventPlatform)
    return accounts[0]
  }
}
