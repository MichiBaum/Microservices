import { Component, OnInit, output, inject } from '@angular/core';
import {InputTextModule} from "primeng/inputtext";
import {PickListModule, PickListMoveToSourceEvent, PickListMoveToTargetEvent} from "primeng/picklist";
import {FloatLabelModule} from "primeng/floatlabel";
import {Button} from "primeng/button";
import {FormsModule} from "@angular/forms";
import {Person, SearchPerson} from "../../core/models/chess/chess.models";
import {ChessService} from "../../core/services/chess.service";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-chess-player-search',
  imports: [
    InputTextModule,
    PickListModule,
    FloatLabelModule,
    Button,
    FormsModule,
    TranslateModule
  ],
  templateUrl: './chess-player-search.component.html',
  styleUrl: './chess-player-search.component.scss'
})
export class ChessPlayerSearchComponent{
  private readonly chessService = inject(ChessService);


  sourcePersons: Person[] = [];
  targetPersons: Person[] = [];

  readonly accountsToAdd = output<Person>();
  readonly accountsToRemove = output<Person>();

  lastname: string = "";
  firstname: string = "";


  search() {
    let searchPerson = {firstname: this.firstname, lastname: this.lastname} as SearchPerson
    this.chessService.searchPersons(searchPerson).subscribe(persons => // TODO check if persons are already in source or target
      this.sourcePersons = [ ...persons])

  }

  onMoveToSelected(event : PickListMoveToTargetEvent) {
    event.items.forEach(item => {
      this.accountsToAdd.emit(item as Person)
    })
  }

  onMoveToSource(event: PickListMoveToSourceEvent) {
    event.items.forEach(item => {
      this.accountsToRemove.emit(item as Person)
    })
  }
}
