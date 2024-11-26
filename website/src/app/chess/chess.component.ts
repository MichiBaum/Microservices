import {Component, OnInit} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ChessPlayerSearchComponent} from "./chess-player-search/chess-player-search.component";
import {ChessAccountsComponent} from "./chess-accounts/chess-accounts.component";
import {ChessStatisticComponent} from "./chess-statistic/chess-statistic.component";
import {Person} from "../core/models/chess/chess.models";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {ChessNewsComponent} from "./chess-news/chess-news.component";
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {ChessService} from "../core/services/chess.service";
import {LanguageConfig} from "../core/config/language.config";
import {TranslateService} from "@ngx-translate/core";
import {combineLatest, Observable} from "rxjs";
import {ChessEvent} from "../core/models/chess/chess-event.models";
import {PermissionService} from "../core/services/permission.service";
import {Permissions} from "../core/config/permissions";

@Component({
  selector: 'app-chess',
  standalone: true,
  imports: [
    SplitterModule,
    MenubarModule
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
    this.chessService.events().subscribe(events => {
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

    this.menuItems = [
      {
        label: this.translate.instant('chess.navigation.news'),
        icon: 'pi pi-fw pi-newspaper',
        routerLink: '/chess/news',
      },
      {
        label: this.translate.instant('chess.navigation.events'),
        icon: 'pi pi-fw pi-newspaper',
        items: menuEvents
      },
      {
        label: this.translate.instant('chess.navigation.player-analysis'),
        icon: 'pi pi-fw pi-chart-bar',
        routerLink: '/chess/player-analysis',
        visible: this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE]),
      },
      {
        label: this.translate.instant('chess.navigation.settings'),
        icon: 'pi pi-fw pi-cog',
        routerLink: '/chess/settings',
        visible: this.permissionService.hasAnyOf([Permissions.CHESS_SERVICE_ADMIN]),
      },
    ];
  }
}
