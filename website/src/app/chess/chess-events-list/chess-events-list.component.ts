import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {ChessEvent} from "../../core/models/chess/chess-event.models";

@Component({
  selector: 'app-chess-events-list',
  standalone: true,
  imports: [],
  templateUrl: './chess-events-list.component.html',
  styleUrl: './chess-events-list.component.scss'
})
export class ChessEventsListComponent implements OnInit {

  events: ChessEvent[] = [];
  eventsByCategory: Map<string, any[]> | undefined;

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.events().subscribe(events => {
      let categories = events
        .map((event) => event.categories)
        .flatMap(event => event)
      let uniqueCategories = [...new Set(categories)];
      let eventsByCategory = new Map<string, any[]>();

      uniqueCategories.forEach(category => {
        const categoryName = category.id; // assuming `name` is a property of `ChessEventCategory`
        eventsByCategory.set(
          categoryName,
          events.filter(event => event.categories.some(c => c.id === categoryName))
        );
      });
      this.eventsByCategory = eventsByCategory;
    })
  }



}
