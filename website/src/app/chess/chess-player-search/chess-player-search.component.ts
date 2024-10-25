import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {InputTextModule} from "primeng/inputtext";
import {PickListModule, PickListMoveToSourceEvent, PickListMoveToTargetEvent} from "primeng/picklist";
import {FloatLabelModule} from "primeng/floatlabel";
import {Button} from "primeng/button";
import {FormsModule} from "@angular/forms";
import {Account, Person, SearchPerson} from "../../core/models/chess.models";
import {ChessService} from "../../core/services/chess.service";

@Component({
  selector: 'app-chess-player-search',
  standalone: true,
  imports: [
    InputTextModule,
    PickListModule,
    FloatLabelModule,
    Button,
    FormsModule
  ],
  templateUrl: './chess-player-search.component.html',
  styleUrl: './chess-player-search.component.scss'
})
export class ChessPlayerSearchComponent implements OnInit{

  sourcePersons: Person[] = [];
  targetPersons: Person[] = [];

  @Output()
  accountsToAdd = new EventEmitter<Person>();
  @Output()
  accountsToRemove = new EventEmitter<Person>();

  lastname: string = "";
  firstname: string = "";

  constructor(
    private chessService: ChessService
  ) {}

  ngOnInit() {
  }

  search() {
    let searchPerson = {firstname: this.firstname, lastname: this.lastname} as SearchPerson
    this.chessService.search(searchPerson).subscribe(persons => // TODO check if persons are already in source or target
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
