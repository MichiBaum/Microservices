import {Component, computed, inject, signal} from '@angular/core';
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
import {rxResource} from "@angular/core/rxjs-interop";

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
export class ChessUpdateGameComponent {
  private readonly chessService = inject(ChessService);

  events = rxResource({
    loader: () => this.chessService.events()
  })
  selectedEvent = signal<ChessEvent | undefined>(undefined)

  persons = computed(() => this.selectedEvent()?.participants ?? [])
  selectedPersons = signal<Person[]>([]);

  accounts = computed(() => this.selectedPersons().flatMap(value => value.accounts))
  selectedAccounts = signal<Account[]>([]);

  accountTableSearch: string = "";

  clear(table: Table) {
    table.clear();
  }

  onEventSelect(event: ChessEvent | undefined) {
    this.selectedEvent.set(event)
  }

  onPersonsSelect(persons: Person[]) {
    this.selectedPersons.set(persons)
  }

  onSelectedAccountsChange() {

  }
}
