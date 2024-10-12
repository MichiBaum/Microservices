import { Component } from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ChessPlayerSearchComponent} from "./chess-player-search/chess-player-search.component";
import {ChessAccountsComponent} from "./chess-accounts/chess-accounts.component";
import {ChessStatisticComponent} from "./chess-statistic/chess-statistic.component";
import {Account, Person} from "../core/models/chess.models";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

@Component({
  selector: 'app-chess',
  standalone: true,
  imports: [
    SplitterModule,
    ChessPlayerSearchComponent,
    ChessAccountsComponent,
    ChessStatisticComponent
  ],
  templateUrl: './chess.component.html',
  styleUrl: './chess.component.scss'
})
export class ChessComponent {

  persons: Person[] = []

  constructor(private headerService: HeaderService) {
    this.headerService.changeTitle(Sides.chess.translationKey)
  }

  onAccountsToAdd(person: Person) {
    this.persons = [...this.persons, person]
  }

  onAccountsToRemove(person: Person) {
    const newAccounts = this.persons.filter(a => a.id !== person.id);
    this.persons = [...newAccounts]
  }
}
