import {Component, computed, inject, OnDestroy, OnInit, Signal, signal, WritableSignal} from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
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
import {Avatar} from "primeng/avatar";
import {ChessEvent} from "../../core/models/chess/chess.models";
import {toObservable} from "@angular/core/rxjs-interop";
import {Subscription} from "rxjs";
import {MetaDataHolder, MetaService} from "../../core/services/meta.service";

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
    Avatar,
  ],
  templateUrl: './chess-event.component.html',
  styleUrl: './chess-event.component.scss'
})
export class ChessEventComponent implements OnInit, OnDestroy {
  private _sanitizer = inject(DomSanitizer);
  private readonly route = inject(ActivatedRoute);
  private readonly navigationService = inject(RouterNavigationService);
  private readonly metaService = inject(MetaService);

  private oldMeta: MetaDataHolder = this.metaService.defaultHolder;

  event: WritableSignal<ChessEvent | undefined> = signal(undefined)

    eventObservable$ = toObservable(this.event);
    eventSubscription: Subscription | undefined = undefined

  embedUrl: Signal<SafeResourceUrl | undefined> = computed(() => {
    const event = this.event();
    if(event === undefined)
      return undefined
    if(event.embedUrl == undefined || event.embedUrl === "")
      return undefined
    return this._sanitizer.bypassSecurityTrustResourceUrl(event.embedUrl)
  })
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

      this.oldMeta = {
          description: this.metaService.getDescription(),
          keywords: this.metaService.getKeywords(),
      }
      this.eventSubscription = this.eventObservable$.subscribe(event => {
          if(event !== undefined){
              let categories = event.categories.map(value => value.title);
              this.metaService.updateDescription(`Chess event ${event.title} (${categories.join(',')}) is taking place from ${event.dateFrom} to ${event.dateTo} in ${event.location}.`)
              this.metaService.setKeyWords(["Chess Event", "Chess", "Event", `${event.title}`, ...categories, `${event.dateFrom}`, `${event.dateTo}`, `${event.location}`])
          }
      })
  }

  ngOnDestroy() {
    this.eventSubscription?.unsubscribe()
    this.metaService.setNewAndGetOld(this.oldMeta)
  }

  openEvent() {
    let event = this.event();
    if(event && event?.url && event?.url !== "")
      this.navigationService.open(event.url)
  }


}
