import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {NgIf} from "@angular/common";
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {ChessEvent} from "../../../core/models/chess/chess-event.models";
import {RouterNavigationService} from "../../../core/services/router-navigation.service";
import {EventParticipant} from "../../../core/models/chess/event-participant.model";
import {ChessService} from "../../../core/services/chess.service";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-chess-event-participants',
  standalone: true,
  imports: [
    Button,
    CardModule,
    NgIf,
    PrimeTemplate,
    TableModule,
    TranslateModule
  ],
  templateUrl: './chess-event-participants.component.html',
  styleUrl: './chess-event-participants.component.scss'
})
export class ChessEventParticipantsComponent implements OnChanges {
  @Input() event: ChessEvent | undefined;
  participants: EventParticipant[] | undefined;

  constructor(
    private readonly navigationService: RouterNavigationService,
    private readonly chessService: ChessService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['event'] && changes['event'].currentValue){
      this.chessService.eventParticipants(changes['event'].currentValue.id).subscribe(p => {
        this.participants = [...p]
      })
    }
  }

  openFide(fideId: string) {
    this.navigationService.open("https://ratings.fide.com/profile/" + fideId)
  }

}
