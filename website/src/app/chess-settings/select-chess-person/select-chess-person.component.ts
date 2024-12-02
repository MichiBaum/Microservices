import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ChessEvent, Person} from "../../core/models/chess/chess.models";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {PrimeTemplate} from "primeng/api";
import {Table, TableModule} from "primeng/table";

@Component({
  selector: 'app-select-chess-person',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    PaginatorModule,
    PrimeTemplate,
    TableModule
  ],
  templateUrl: './select-chess-person.component.html',
  styleUrl: './select-chess-person.component.scss'
})
export class SelectChessPersonComponent {
  @Input()
  persons: Person[] = [];

  @Output()
  selectedPersonsEmitter: EventEmitter<Person[]> = new EventEmitter()

  selectedPersons: Person[] = [];
  tableSearch: string = "";

  constructor() {
  }

  onSelectionChange() {
      this.selectedPersonsEmitter.emit(this.selectedPersons)
  }

  clear(table: Table) {
    table.clear();
    this.tableSearch = ''
  }
}
