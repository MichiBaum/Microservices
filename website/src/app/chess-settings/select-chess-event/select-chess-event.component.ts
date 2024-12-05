import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Table, TableModule} from "primeng/table";
import {ChessEvent} from "../../core/models/chess/chess.models";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-select-chess-event',
  standalone: true,
  imports: [
    TableModule,
    InputTextModule,
    FormsModule,
    FaIconComponent
  ],
  templateUrl: './select-chess-event.component.html',
  styleUrl: './select-chess-event.component.scss'
})
export class SelectChessEventComponent {

  @Input()
  events: ChessEvent[] = [];

  @Output()
  selectedEventEmitter: EventEmitter<ChessEvent> = new EventEmitter()

  selectedEvent: ChessEvent | undefined;
  tableSearch: string = "";

  constructor() {
  }

  onSelectionChange() {
    if(this.selectedEvent) {
      this.selectedEventEmitter.emit(this.selectedEvent)
    }
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
