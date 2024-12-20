import { Component, OnInit, inject } from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {ActivatedRoute} from '@angular/router';
import {ChessService} from "../../core/services/chess.service";
import {CardModule} from "primeng/card";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {NgIf} from "@angular/common";
import {ChessEventParticipantsComponent} from "./chess-event-participants/chess-event-participants.component";
import {ChessEvent} from "../../core/models/chess/chess.models";
import {ChessEventGamesComponent} from "./chess-event-games/chess-event-games.component";
import {DividerModule} from "primeng/divider";
import {Tab, TabList, TabPanel, TabPanels, Tabs} from "primeng/tabs";
import {faCircleInfo} from "@fortawesome/free-solid-svg-icons";
import {Avatar} from "primeng/avatar";

@Component({
  selector: 'app-chess-events',
  imports: [
    CardModule,
    Button,
    TranslateModule,
    TableModule,
    NgIf,
    ChessEventParticipantsComponent,
    ChessEventGamesComponent,
    DividerModule,
    Tabs,
    TabList,
    Tab,
    TabPanel,
    TabPanels,
    Avatar,
  ],
  templateUrl: './chess-event.component.html',
  styleUrl: './chess-event.component.scss'
})
export class ChessEventComponent implements OnInit {
  private _sanitizer = inject(DomSanitizer);
  private readonly route = inject(ActivatedRoute);
  private readonly chessService = inject(ChessService);
  private readonly navigationService = inject(RouterNavigationService);


  embedUrl: SafeResourceUrl = "";
  event: ChessEvent | undefined;

  gamesTabDisabled: boolean = true;


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

  getCategories(event: ChessEvent) {
    return event.categories.map(value => value.title).join(", ")
  }

  changeGamesTabVisibility(hasContent: boolean){
    this.gamesTabDisabled = !hasContent;
  }

  protected readonly faCircleInfo = faCircleInfo;
}
