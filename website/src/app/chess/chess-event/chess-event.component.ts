import {Component, computed, inject, OnInit, Signal, signal, WritableSignal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CardModule} from "primeng/card";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {NgIf} from "@angular/common";
import {ChessEventParticipantsComponent} from "./chess-event-participants/chess-event-participants.component";
import {ChessEventGamesComponent} from "./chess-event-games/chess-event-games.component";
import {DividerModule} from "primeng/divider";
import {TabsModule} from "primeng/tabs";
import {ChessEvent} from "../../core/models/chess/chess.models";

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
    TabsModule,
  ],
  templateUrl: './chess-event.component.html',
  styleUrl: './chess-event.component.css'
})
export class ChessEventComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly navigationService = inject(RouterNavigationService);

  event: WritableSignal<ChessEvent | undefined> = signal(undefined)

  categories: Signal<string> = computed(() => {
    const event = this.event();
    if(event == undefined)
      return ""
    return event.categories.map(value => value.title).join(", ")
  })

  gamesTabDisabled = signal(true)

  ngOnInit(): void {
      this.route.data.subscribe(({ event }) => {
          this.event.set(event)
      })
  }

  openEvent() {
    let event = this.event();
    if(event && event?.url && event?.url !== "")
      this.navigationService.open(event.url)
  }


}


