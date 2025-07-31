import {Component, inject, input, OnInit, output, signal} from '@angular/core';
import {TableModule} from "primeng/table";
import {ChessEvent, ChessEventCategory} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";

import {ConfirmDialogModule} from "primeng/confirmdialog";
import {FilterService, SelectItem} from "primeng/api";

@Component({
  selector: 'app-select-chess-event',
  imports: [
    TableModule,
    InputTextModule,
    FormsModule,
    FaIconComponent,
    ConfirmDialogModule
],
  templateUrl: './select-chess-event.component.html',
  styleUrl: './select-chess-event.component.css'
})
export class SelectChessEventComponent implements OnInit{
  private readonly filterService = inject(FilterService);

  readonly events = input<ChessEvent[]>([]);
  readonly selectedEventEmitter = output<ChessEvent | undefined>();

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

}


