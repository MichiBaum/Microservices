import {Component, OnInit} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ChessEvent} from "../core/models/chess/chess.models";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {ChessService} from "../core/services/chess.service";
import {LanguageConfig} from "../core/config/language.config";
import {TranslateService} from "@ngx-translate/core";
import {PermissionService} from "../core/services/permission.service";
import {Permissions} from "../core/config/permissions";
import {Ripple} from "primeng/ripple";
import {BadgeModule} from "primeng/badge";
import {NgClass, NgIf} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faChartLine, faGears, faNewspaper, faPerson} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-chess',
  standalone: true,
  imports: [
    SplitterModule,
    MenubarModule,
    Ripple,
    BadgeModule,
    NgClass,
    NgIf,
    FaIconComponent
  ],
  templateUrl: './chess.component.html',
  styleUrl: './chess.component.scss'
})
export class ChessComponent implements OnInit{

  menuItems: MenuItem[] = [];
  events: ChessEvent[] = [];

  constructor(
    private readonly headerService: HeaderService,
    private readonly chessService: ChessService,
    private readonly languageConfig: LanguageConfig,
    private readonly translate: TranslateService,
    private readonly permissionService: PermissionService,
  ) { }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.chess.translationKey)
    this.chessService.eventsRecentUpcoming().subscribe(events => { // TODO only get "recent" events +- 1 Month
      this.events = [...events];
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
          routerLink: '/chess/events/' + event.id,
        } as MenuItem
      )
    );
    menuEvents.push({
      label: "All",
      routerLink: '/chess/events/'
    } as MenuItem)


    this.menuItems = [
      {
        label: this.translate.instant('chess.navigation.news'),
        customIcon: faNewspaper,
        routerLink: '/chess/news',
      },
      {
        label: this.translate.instant('chess.navigation.events'),
        customIcon: faNewspaper,
        items: menuEvents
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
            label: "Persons",
            routerLink: "/chess/settings/persons"
          },
          {
            label: "Accounts",
            routerLink: "/chess/settings/accounts"
          },
          {
            label: "Events",
            routerLink: "/chess/settings/events"
          },
          {
            label: "Games",
            routerLink: "/chess/settings/games"
          }
        ]
      },
    ];
  }
}
