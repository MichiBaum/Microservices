import {Component, OnInit} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/services/chess.service";
import {Account, ChessEvent, Person} from "../../core/models/chess/chess.models";
import {MultiSelectModule} from "primeng/multiselect";
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {Table, TableModule} from "primeng/table";
import {IconFieldModule} from "primeng/iconfield";
import {InputIconModule} from "primeng/inputicon";
import {InputTextModule} from "primeng/inputtext";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {SelectChessEventComponent} from "../select-chess-event/select-chess-event.component";
import {SelectChessPersonComponent} from "../select-chess-person/select-chess-person.component";

@Component({
  selector: 'app-chess-update-game',
  imports: [
    FieldsetModule,
    MultiSelectModule,
    FormsModule,
    NgIf,
    TableModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    Button,
    CardModule,
    SelectChessEventComponent,
    SelectChessPersonComponent
  ],
  templateUrl: './chess-update-game.component.html',
  styleUrl: './chess-update-game.component.scss'
})
export class ChessUpdateGameComponent implements OnInit {

  events: ChessEvent[] = [];
  selectedEvent: ChessEvent | undefined;

  persons: Person[] = [];
  selectedPersons: Person[] = [];

  accounts: Account[] = [];
  selectedAccounts: Account[] = [];
  accountTableSearch: string = "";

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.events().subscribe(events => {
      this.events = [...events]
    })
  }

  onSelectedAccountsChange() {

  }

  clear(table: Table) {
    table.clear();
  }

  onEventSelect(event: ChessEvent | undefined) {
    if(event == undefined){
      this.selectedEvent = undefined;
      this.persons = [...[]]
      this.selectedPersons = [...[]]
      return
    }
    this.selectedEvent = {...event}
    this.persons = [...event.participants]
    this.selectedPersons = [...[]]
  }

  onPersonsSelect(persons: Person[]) {
    this.selectedPersons = [...persons]
    this.accounts = [...persons.flatMap(value => value.accounts)]
    this.selectedAccounts = [...[]]
  }
}
