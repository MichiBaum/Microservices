import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {ChessEvent, ChessEventCategory} from "../../core/models/chess/chess-event.models";
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-chess-events-list',
  standalone: true,
  imports: [
    CardModule,
    NgForOf,
    NgIf,
    RouterLink
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
}
