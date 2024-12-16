import {Component, OnInit} from '@angular/core';
import {
  faCalendarDay, faCalendarPlus, faCalendarXmark,
  faChartLine,
  faChessQueen,
  faGears,
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
import {faClock} from "@fortawesome/free-regular-svg-icons";

@Component({
  selector: 'app-chess-navigation',
  standalone: true,
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
  protected readonly faChessQueen = faChessQueen;

  menuItems: MenuItem[] = [];
  events: ChessEvent[] = [];

  constructor(
    private readonly chessService: ChessService,
    private readonly languageConfig: LanguageConfig,
    private readonly translate: TranslateService,
    private readonly permissionService: PermissionService,
  ) { }

  ngOnInit(): void {
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
          eventCategories: event.categories,
          routerLink: '/chess/events/' + event.id,
          tag: this.getEventIcon(event),
          tagColor: this.getEventIconColor(event),
        } as MenuItem
      )
    );
    menuEvents.push({
      label: this.translate.instant('chess.navigation.all-events'),
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

  getEventIconColor(event: ChessEvent){
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom)
      const dateTo = new Date(event.dateTo)
      const current = new Date()
      if(dateTo > current && dateFrom < current){ return "color: green"}
      if(dateTo < current ){ return "color: red"}
      if(dateFrom > current){ return "color: #0688fb"}
    }
    return ""
  }

  getEventIcon(event: ChessEvent) {
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom)
      const dateTo = new Date(event.dateTo)
      const current = new Date()
      if(dateTo > current && dateFrom < current){ return faCalendarDay}
      if(dateTo < current ){ return faCalendarXmark}
      if(dateFrom > current){ return faCalendarPlus}
    }
    return faClock;
  }

}
