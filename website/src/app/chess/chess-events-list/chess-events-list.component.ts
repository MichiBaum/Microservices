import { Component, OnInit, inject } from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {ChessEventCategoryWithEvents} from "../../core/models/chess/chess.models";
import {TimelineModule} from "primeng/timeline";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {ScrollTopModule} from "primeng/scrolltop";
import {EventIconPipe} from "../../core/pipes/event-icon.pipe";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";

@Component({
  selector: 'app-chess-events-list',
  imports: [
    CardModule,
    NgForOf,
    RouterLink,
    TimelineModule,
    FaIconComponent,
    ScrollTopModule,
    NgIf,
    EventIconPipe,
    EventIconColorPipe
  ],
  templateUrl: './chess-events-list.component.html',
  styleUrl: './chess-events-list.component.scss'
})
export class ChessEventsListComponent implements OnInit {
  private readonly chessService = inject(ChessService);


  categories: ChessEventCategoryWithEvents[] = []

  ngOnInit(): void {
    this.chessService.eventCategoriesWithEvents().subscribe(categories => {
      categories.forEach(category => {
        if (category.events) {
          category.events = this.chessService.sortEvents(category.events)
        }
      });
      this.categories = [...categories];
    })
  }

}
