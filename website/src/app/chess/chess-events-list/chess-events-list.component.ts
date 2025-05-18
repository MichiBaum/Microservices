import {Component, computed, inject, signal, Signal} from '@angular/core';
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
import {Fieldset} from "primeng/fieldset";
import {TranslatePipe} from "@ngx-translate/core";
import {MultiSelect} from "primeng/multiselect";
import {FormsModule} from "@angular/forms";
import {ChessEventCategoryWithEvents} from "../../core/models/chess/chess.models";

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
        EventIconColorPipe,
        Fieldset,
        TranslatePipe,
        MultiSelect,
        FormsModule
    ],
  templateUrl: './chess-events-list.component.html',
  styleUrl: './chess-events-list.component.css'
})
export class ChessEventsListComponent {
  private readonly chessService = inject(ChessService);

  categories = rxResource({
    loader: () => this.chessService.eventCategoriesWithEvents(),
    defaultValue: []
  })
  categoriesFilteredAndSorted = computed(() => {
    const categories = this.categories.value()
    const _categories = JSON.parse(JSON.stringify(categories)) as ChessEventCategoryWithEvents[] // deeeeep copy
    return _categories
        .sort((a, b) => a.title.localeCompare(b.title))
  })
  categoriesAndEventsSorted = computed(() => {
    const categoriesSorted: ChessEventCategoryWithEvents[] = this.categoriesFilteredAndSorted()
    const yearsSelected = this.selectedYears()
    const _categoriesSorted = JSON.parse(JSON.stringify(categoriesSorted)) as ChessEventCategoryWithEvents[] // deeeeep copy
    return _categoriesSorted.map(category => {
      if (category.events) {
        category.events = category.events.filter(event => {
            if (yearsSelected.length == 0) return true;
            let eventYear = event.dateFrom?.substring(0, 4);
            if(eventYear == undefined) return true;
            return yearsSelected.includes(eventYear);
        })
        category.events = this.chessService.sortEvents(category.events)
      }
      return category
    })
  })
  years: Signal<string[]> = computed(() => {
      const categories = this.categories.value()
      if(categories == undefined) return []
      return categories.flatMap(value => value.events)
          .map(value => value.dateFrom)
          .filter(value => value != undefined)
          .map(value => value.substring(0, 4))
          .filter((value, index, array) => array.indexOf(value) === index)
          .sort((a, b) => b.localeCompare(a));
  });
  selectedYears = signal<string[]>([]);

}


