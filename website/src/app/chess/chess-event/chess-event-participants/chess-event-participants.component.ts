import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
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
export class ChessEventParticipantsComponent implements OnChanges {
  @Input() event: ChessEvent | undefined;
  participants: Person[] | undefined;

  constructor(
    private readonly navigationService: RouterNavigationService,
    private readonly chessService: ChessService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['event'] && changes['event'].currentValue){
      this.chessService.eventParticipants(changes['event'].currentValue.id).subscribe(persons => {
        this.participants = [...persons]
      })
    }
  }

  openFide(fideId: string) {
    this.navigationService.open("https://ratings.fide.com/profile/" + fideId)
  }

}
