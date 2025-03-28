import {Component, computed, inject} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {TimelineModule} from "primeng/timeline";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {ScrollTopModule} from "primeng/scrolltop";
import {EventIconPipe} from "../../core/pipes/event-icon.pipe";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";
import {rxResource} from "@angular/core/rxjs-interop";

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
export class ChessEventsListComponent {
  private readonly chessService = inject(ChessService);


  categories = rxResource({
    loader: () => this.chessService.eventCategoriesWithEvents()
  })
  categoriesSorted = computed(() => {
    const _categories = this.categories.value()
    if(_categories == undefined) return []
    return _categories.sort((a, b) => a.title.localeCompare(b.title))
  })
  categoriesAndEventsSorted = computed(() => {
    const _categoriesSorted = this.categoriesSorted()
    if(_categoriesSorted == undefined) return []
    return _categoriesSorted.map(category => {
      if (category.events) {
        category.events = this.chessService.sortEvents(category.events)
      }
      return category
    })
  })

}
