import {Component, computed, inject} from '@angular/core';
import {
    faAngleDown,
    faCalendarDays, faChess, faChessKing,
    faChessQueen,
    faGears,
    faHouse,
    faNewspaper
} from "@fortawesome/free-solid-svg-icons";
import {Badge} from "primeng/badge";
import {Divider} from "primeng/divider";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {Menubar} from "primeng/menubar";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Ripple} from "primeng/ripple";
import {Tag} from "primeng/tag";
import {RouterLink} from "@angular/router";
import {ChessService} from "../../core/api-services/chess.service";
import {TranslatePipe} from "@ngx-translate/core";
import {PermissionService} from "../../core/services/permission.service";
import {MenuItem} from "primeng/api";
import {Permissions} from "../../core/config/permissions";
import {ChessEvent, ChessOpening} from "../../core/models/chess/chess.models";
import {EventIconPipe} from 'src/app/core/pipes/event-icon.pipe';
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";
import {rxResource} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-chess-navigation',
  providers: [EventIconPipe, EventIconColorPipe],
  imports: [
    Badge,
    Divider,
    FaIconComponent,
    Menubar,
    NgIf,
    Ripple,
    Tag,
    RouterLink,
    NgClass,
    TranslatePipe
  ],
  templateUrl: './chess-navigation.component.html',
  styleUrl: './chess-navigation.component.scss'
})
export class ChessNavigationComponent {
  private readonly chessService = inject(ChessService);
  private readonly permissionService = inject(PermissionService);
  private readonly eventIcon = inject(EventIconPipe);
  private readonly eventIconColor = inject(EventIconColorPipe);

    protected readonly faChessKing = faChessKing;
  protected readonly faAngleDown = faAngleDown;


  events = rxResource({
    loader: () => this.chessService.eventsRecentUpcoming()
  })
  startOpenings = rxResource({
    loader: () =>  this.chessService.startingOpenings()
  })
  popularOpenings = rxResource({
    loader: () =>  this.chessService.popularOpenings()
  })
  sortedEvents = computed(() => {
    const events = this.events.value()
    if(events == undefined)
      return []
    return this.chessService.sortEvents(events)
  })
  sortedPopularOpenings = computed(() => {
    const openings = this.popularOpenings.value()
    if(openings == undefined)
      return []
    return openings.sort((a, b) => a.ranking - b.ranking)
  })
  menuItems = computed(() => {
    const sorted = this.sortedEvents()
      const openings = this.startOpenings.value() ?? []
      const popularOpenings = this.sortedPopularOpenings()
    return this.createMenu(sorted, openings, popularOpenings)
  })

  private createMenu(events: ChessEvent[], openings: ChessOpening[], popularOpenings: ChessOpening[]) {
      const menuEvents = this.createMenuEvents(events);
      const menuOpenings = this.createMenuOpenings(openings);
      const menuPopularOpenings = this.createMenuOpenings(popularOpenings);

      return [
      {
        label: 'chess.navigation.home',
        customIcon: faHouse,
        routerLink: '/chess',
      },
      {
        label: 'chess.navigation.news',
        customIcon: faNewspaper,
        routerLink: '/chess/news',
      },
      {
        label: 'chess.navigation.events',
        customIcon: faCalendarDays,
        items: menuEvents,
      },
        {
            label: 'chess.navigation.openings',
            customIcon: faChess,
            items: [
                {
                    label: 'chess.navigation.start-openings',
                    items: menuOpenings,
                },
                {
                    label: 'chess.navigation.popular-openings',
                    items: menuPopularOpenings,
                }
            ]
        },
      {
        label: 'chess.navigation.settings',
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
          },
          {
            label: "Update Engine",
            routerLink: "/chess/settings/engines"
          },
          {
            label: "Update Openings",
            routerLink: "/chess/settings/openings"
          },
          {
            label: "Fide import",
            routerLink: "/chess/settings/fide-import"
          }
        ]
      },
    ] as MenuItem[];
  }

    private createMenuEvents(events: ChessEvent[]) {
        const menuEvents = events.map(event => (
                {
                    label: event.title,
                    subText: event.categories.map(category => category.title).join(', '),
                    routerLink: '/chess/events/' + event.id,
                    tag: this.eventIcon.transform(event),
                    tagColor: this.eventIconColor.transform(event),
                } as MenuItem
            )
        );
        menuEvents.push({
            label: 'chess.navigation.all-events',
            routerLink: '/chess/events/'
        } as MenuItem)
        return menuEvents;
    }

    private createMenuOpenings(openings: ChessOpening[]){
      return openings.map(opening => (
          {
            label: opening.name,
            routerLink: '/chess/openings/' + opening.id,
          } as MenuItem
      ))
    }

}
