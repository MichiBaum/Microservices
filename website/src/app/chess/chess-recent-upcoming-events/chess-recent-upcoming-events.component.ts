import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {ChessEvent} from "../../core/models/chess/chess.models";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {PrimeTemplate} from "primeng/api";
import {Timeline} from "primeng/timeline";
import {RouterLink} from "@angular/router";
import {EventIconPipe} from "../../core/pipes/event-icon.pipe";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";

@Component({
  selector: 'app-chess-recent-upcoming-events',
  standalone: true,
  imports: [
    FaIconComponent,
    PrimeTemplate,
    Timeline,
    RouterLink,
    EventIconPipe,
    EventIconColorPipe
  ],
  templateUrl: './chess-recent-upcoming-events.component.html',
  styleUrl: './chess-recent-upcoming-events.component.scss'
})
export class ChessRecentUpcomingEventsComponent implements OnInit{

  events: ChessEvent[] = []

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.eventsRecentUpcoming().subscribe(events => {
      const sorted = this.chessService.sortEvents(events)
      this.events = [...sorted];
    })
  }

}
