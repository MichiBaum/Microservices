import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {ActivatedRoute} from '@angular/router';
import {ChessService} from "../../core/services/chess.service";
import {ChessEvent} from "../../core/models/chess/chess-event.models";
import {CardModule} from "primeng/card";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {TabViewModule} from "primeng/tabview";
import {TranslateModule} from "@ngx-translate/core";
import {EventParticipant} from "../../core/models/chess/event-participant.model";
import {TableModule} from "primeng/table";
import {NgIf} from "@angular/common";
import {ChessEventParticipantsComponent} from "./chess-event-participants/chess-event-participants.component";

@Component({
  selector: 'app-chess-events',
  standalone: true,
  imports: [
    CardModule,
    Button,
    TabViewModule,
    TranslateModule,
    TableModule,
    NgIf,
    ChessEventParticipantsComponent
  ],
  templateUrl: './chess-event.component.html',
  styleUrl: './chess-event.component.scss'
})
export class ChessEventComponent implements OnInit {

  embedUrl: SafeResourceUrl = "";
  event: ChessEvent | undefined;


  constructor(
    private _sanitizer: DomSanitizer,
    private readonly route: ActivatedRoute,
    private readonly chessService: ChessService,
    private readonly navigationService: RouterNavigationService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if(id !== null) {
        this.chessService.event(id).subscribe(ev => {
          this.event = ev
          this.setIframeUrl(this.event)
        })
      }
    });
  }

  setIframeUrl(event: ChessEvent): void {
    if(event.embedUrl === undefined) return
    this.embedUrl = this._sanitizer.bypassSecurityTrustResourceUrl(event.embedUrl);
  }

  openEvent() {
    if(this.event && this.event.url)
      this.navigationService.open(this.event.url)
  }

}
