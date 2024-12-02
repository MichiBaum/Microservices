import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {ChessEvent, ChessEventCategory} from "../../core/models/chess/chess.models";
import {TimelineModule} from "primeng/timeline";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faClock} from "@fortawesome/free-regular-svg-icons";
import {faCalendarDay, faCalendarPlus, faCalendarXmark, faQuestion} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-chess-events-list',
  standalone: true,
  imports: [
    CardModule,
    NgForOf,
    NgIf,
    RouterLink,
    TimelineModule,
    FaIconComponent
  ],
  templateUrl: './chess-events-list.component.html',
  styleUrl: './chess-events-list.component.scss'
})
export class ChessEventsListComponent implements OnInit {

  events: ChessEvent[] = [];
  categories: ChessEventCategory[] = [];

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.events().subscribe(events => {
      this.events = [...events];
      let categories = events
        .map(event => event.categories ?? [])
        .flatMap(category => category)
        .filter(Boolean); // Filters out any falsey values (undefined or null)

      let uniqueCategoriesMap = new Map<string, ChessEventCategory>();
      categories.forEach(category => uniqueCategoriesMap.set(category.id, category));

      let uniqueCategories = Array.from(uniqueCategoriesMap.values());
      console.log(uniqueCategories);
      this.categories = [...uniqueCategories];
    });
  }

  eventOfCategory(event: ChessEvent, category: ChessEventCategory) {
    return event.categories?.some(x => x.id == category.id) ?? false;
  }

  eventsOfCategory(events: ChessEvent[], category: ChessEventCategory) {
    return events.filter(event => this.eventOfCategory(event, category));
  }

  getColor(event: ChessEvent){
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

  getIcon(event: ChessEvent) {
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
