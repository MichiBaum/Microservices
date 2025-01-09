import {Component, computed, inject, OnDestroy, OnInit, Signal, signal, WritableSignal} from '@angular/core';
import {DomSanitizer, Meta, SafeResourceUrl, Title} from "@angular/platform-browser";
import {ActivatedRoute} from '@angular/router';
import {ChessService} from "../../core/api-services/chess.service";
import {CardModule} from "primeng/card";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {NgIf} from "@angular/common";
import {ChessEventParticipantsComponent} from "./chess-event-participants/chess-event-participants.component";
import {ChessEventGamesComponent} from "./chess-event-games/chess-event-games.component";
import {DividerModule} from "primeng/divider";
import {Tab, TabList, TabPanel, TabPanels, Tabs} from "primeng/tabs";
import {Avatar} from "primeng/avatar";
import {rxResource} from "@angular/core/rxjs-interop";
import {EMPTY} from "rxjs";
import {MetaService} from "../../core/services/meta.service";

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
export class ChessEventComponent implements OnDestroy {
  private _sanitizer = inject(DomSanitizer);
  private readonly route = inject(ActivatedRoute);
  private readonly chessService = inject(ChessService);
  private readonly navigationService = inject(RouterNavigationService);

  eventId: WritableSignal<string | undefined> = signal("")
  event = rxResource({
    request: () => ({id: this.eventId()}),
    loader: (param) => {
      let id = param.request.id;
      if(id == undefined)
        return EMPTY
      return this.chessService.event(id)
    }
  })
  embedUrl: Signal<SafeResourceUrl | undefined> = computed(() => {
    const event = this.event.value();
    if(event === undefined)
      return undefined
    if(event.embedUrl == undefined || event.embedUrl === "")
      return undefined
    return this._sanitizer.bypassSecurityTrustResourceUrl(event.embedUrl)
  })
  categories: Signal<string> = computed(() => {
    const event = this.event.value();
    if(event == undefined)
      return ""
    return event.categories.map(value => value.title).join(", ")
  })

  gamesTabDisabled = signal(true)

  routeParamsSubscription = this.route.params.subscribe(params => {
    const id = params['id'];
    this.eventId.set(id)
  });

  ngOnDestroy() {
    this.routeParamsSubscription.unsubscribe()
  }

  openEvent() {
    let event = this.event.value();
    if(event && event?.url && event?.url !== "")
      this.navigationService.open(event.url)
  }

}
