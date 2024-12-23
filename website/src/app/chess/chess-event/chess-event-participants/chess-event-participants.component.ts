import {Component, OnChanges, SimpleChanges, input, inject, computed} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {NgIf} from "@angular/common";
import {TableModule} from "primeng/table";
import {RouterNavigationService} from "../../../core/services/router-navigation.service";
import {ChessService} from "../../../core/services/chess.service";
import {TranslateModule} from "@ngx-translate/core";
import {ChessEvent, Person} from "../../../core/models/chess/chess.models";

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
  protected readonly participants = computed(() => this.event()?.participants ?? [])

  openFide(fideId: string) {
    this.navigationService.open("https://ratings.fide.com/profile/" + fideId)
  }

}
