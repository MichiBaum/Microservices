import {Component, OnInit, input, output, inject, signal} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from "primeng/table";
import {ChessEvent, ChessEventCategory, SearchChessEvent} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";
import {NgForOf} from "@angular/common";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {FilterMetadata, FilterService, SelectItem} from "primeng/api";
import {LazyLoad} from "../../core/models/lazy-load.model";

@Component({
  selector: 'app-select-chess-event',
  imports: [
    TableModule,
    InputTextModule,
    FormsModule,
    FaIconComponent,
    NgForOf,
    ConfirmDialogModule
  ],
  templateUrl: './select-chess-event.component.html',
  styleUrl: './select-chess-event.component.scss'
})
export class SelectChessEventComponent implements OnInit{
  private readonly filterService = inject(FilterService);

  readonly events = input<ChessEvent[]>([]);
  readonly selectedEventEmitter = output<ChessEvent | undefined>();
  readonly lazyLoadEventEmitter = output<LazyLoad<SearchChessEvent>>();

  pageSize = 100 // TODO https://github.com/primefaces/primeng/issues/17106
  virtualPageSize = this.pageSize/2

  selectedEvent = signal<ChessEvent | undefined>(undefined);

  matchModeOptions: SelectItem[] = [];
  eventCategoryFilterName = 'anyEventCategoryLike';
  eventUrlFilterName = 'eventUrlPresent';


  ngOnInit(): void {
    this.filterService.register(this.eventCategoryFilterName, (value: any, filter:any): boolean => {
      if (filter === undefined || filter === null || filter.trim() === '') { return true; }
      if (value === undefined || value === null) { return false; }

      const eventCategories = value as ChessEventCategory[];
      const filterText = filter as string;
      return eventCategories.some(value1 => value1.title.toLowerCase().includes(filterText.toLowerCase()))
    });
    this.filterService.register(this.eventUrlFilterName, (value: any, filter:any): boolean => {
      if (filter === undefined || filter === null) { return true; }

      const valueUndefined = value === undefined || value === null || value === '';
      if(filter && !valueUndefined){
        return true;
      }
      if (!filter && valueUndefined) {
        return true;
      }
      return false;
    });
    this.matchModeOptions = [
      { label: 'Any Event Category Like', value: this.eventCategoryFilterName },
      { label: 'Event Url Present', value: this.eventUrlFilterName }
    ];
  }

  onSelectionChange() {
    this.selectedEventEmitter.emit(this.selectedEvent())
  }

  getIcon(url: string) {
    if(url == undefined || url == "")
      return faXmark
    return faCheck
  }

  getColor(url: string) {
    if(url == undefined || url == "")
      return "color: red"
    return "color: green"
  }

  lazyLoad($event: TableLazyLoadEvent) {
    console.log($event)

    const last = $event.last // TODO maybe first + rows??? What happens when filter?
    if(last == undefined){
      return
    }

    const data: SearchChessEvent = {
      title: ($event.filters?.['title'] as FilterMetadata)?.value ?? '',
      category: ($event.filters?.['categories'] as FilterMetadata)?.value ?? '',
      location: ($event.filters?.['location'] as FilterMetadata)?.value ?? '',
      url: ($event.filters?.['url'] as FilterMetadata)?.value ?? '',
      embedUrl: ($event.filters?.['url'] as FilterMetadata)?.value ?? '',
      pageNumber: Math.round(last / this.pageSize),
      pageSize: this.pageSize,
    }

    const lazyLoad: LazyLoad<SearchChessEvent> = {
      data: data,
      event: $event
    }
    this.lazyLoadEventEmitter.emit(lazyLoad)
  }

}
