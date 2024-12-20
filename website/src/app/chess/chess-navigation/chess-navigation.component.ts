import { Component, OnInit, inject } from '@angular/core';
import {
  faCalendarDays,
  faChartLine,
  faChessQueen,
  faGears,
  faHouse,
  faNewspaper,
  faPerson
} from "@fortawesome/free-solid-svg-icons";
import {Badge} from "primeng/badge";
import {Divider} from "primeng/divider";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {Menubar} from "primeng/menubar";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Ripple} from "primeng/ripple";
import {Tag} from "primeng/tag";
import {RouterLink} from "@angular/router";
import {ChessService} from "../../core/services/chess.service";
import {LanguageConfig} from "../../core/config/language.config";
import {TranslateService} from "@ngx-translate/core";
import {PermissionService} from "../../core/services/permission.service";
import {MenuItem} from "primeng/api";
import {Permissions} from "../../core/config/permissions";
import {ChessEvent} from "../../core/models/chess/chess.models";
import { EventIconPipe } from 'src/app/core/pipes/event-icon.pipe';
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";

@Component({
  selector: 'app-chess-navigation',
  providers: [EventIconPipe, EventIconColorPipe],
  imports: [
    Badge,
    Divider,
    FaIconComponent,
    Menubar,
    NgForOf,
    NgIf,
    Ripple,
    Tag,
    RouterLink,
    NgClass
  ],
  templateUrl: './chess-navigation.component.html',
  styleUrl: './chess-navigation.component.scss'
})
export class ChessNavigationComponent implements OnInit{
  private readonly chessService = inject(ChessService);
  private readonly languageConfig = inject(LanguageConfig);
  private readonly translate = inject(TranslateService);
  private readonly permissionService = inject(PermissionService);
  private readonly eventIcon = inject(EventIconPipe);
  private readonly eventIconColor = inject(EventIconColorPipe);

  protected readonly faChessQueen = faChessQueen;

  menuItems: MenuItem[] = [];
  events: ChessEvent[] = [];


  ngOnInit(): void {
    this.chessService.eventsRecentUpcoming().subscribe(events => { // TODO only get "recent" events +- 1 Month
      const sorted = this.chessService.sortEvents(events)
      this.events = [...sorted];
      this.createMenu();
    })

    this.languageConfig.languageChanged.subscribe(() => {
      this.createMenu();
    })
  }

  private createMenu() {
    const menuEvents = this.events.map(event => (
        {
          label: event.title,
          eventCategories: event.categories,
          routerLink: '/chess/events/' + event.id,
          tag: this.eventIcon.transform(event),
          tagColor: this.eventIconColor.transform(event),
        } as MenuItem
      )
    );
    menuEvents.push({
      label: this.translate.instant('chess.navigation.all-events'),
      routerLink: '/chess/events/'
    } as MenuItem)


    this.menuItems = [
      {
        label: this.translate.instant('chess.navigation.home'),
        customIcon: faHouse,
        routerLink: '/chess',
      },
      {
        label: this.translate.instant('chess.navigation.news'),
        customIcon: faNewspaper,
        routerLink: '/chess/news',
      },
      {
        label: this.translate.instant('chess.navigation.events'),
        customIcon: faCalendarDays,
        items: menuEvents,
      },
      {
        label: this.translate.instant('chess.navigation.person'),
        customIcon: faPerson,
        visible: this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE]),
      },
      {
        label: this.translate.instant('chess.navigation.player-analysis'),
        customIcon: faChartLine,
        routerLink: '/chess/player-analysis',
        visible: this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE]),
      },
      {
        label: this.translate.instant('chess.navigation.settings'),
        customIcon: faGears,
        visible: this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE_ADMIN]),
        items: [
          {
            label: "Update Persons",
            routerLink: "/chess/settings/persons"
          },
          {
            label: "Update Accounts",
            routerLink: "/chess/settings/accounts"
          },
          {
            label: "Update Event Categories",
            routerLink: "/chess/settings/event-categories"
          },
          {
            label: "Update Events",
            routerLink: "/chess/settings/events"
          },
          {
            label: "Update Games",
            routerLink: "/chess/settings/games"
          }
        ]
      },
    ];
  }

}
