import {Component, computed, inject} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {PrimeTemplate} from "primeng/api";
import {Timeline} from "primeng/timeline";
import {RouterLink} from "@angular/router";
import {EventIconPipe} from "../../core/pipes/event-icon.pipe";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessEvent} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-recent-upcoming-events',
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
export class ChessRecentUpcomingEventsComponent {
  private readonly chessService = inject(ChessService);

  events = rxResource({
    loader: () => this.chessService.eventsRecentUpcoming()
  })
  eventsSorted = computed(() => {
    const events = this.events.value()
    if(events == undefined) return []
    return this.chessService.sortEvents(events)
  })

    joinEventCategories(event: ChessEvent): string {
        return event.categories.map(category => category.title).join(', ');
    }
}
