import {Component} from '@angular/core';
import {Person} from "../../core/models/chess/chess.models";
import {SplitterModule} from "primeng/splitter";
import {ChessPlayerSearchComponent} from "../chess-player-search/chess-player-search.component";
import {ChessAccountsComponent} from "../chess-accounts/chess-accounts.component";
import {ChessStatisticComponent} from "../chess-statistic/chess-statistic.component";

@Component({
  selector: 'app-chess-player-analysis',
  imports: [
    SplitterModule,
    ChessPlayerSearchComponent,
    ChessAccountsComponent,
    ChessStatisticComponent
  ],
  templateUrl: './chess-player-analysis.component.html',
  styleUrl: './chess-player-analysis.component.scss'
})
export class ChessPlayerAnalysisComponent {
  persons: Person[] = []

  onAccountsToAdd(person: Person) {
    this.persons = [...this.persons, person]
  }

  onAccountsToRemove(person: Person) {
    const newAccounts = this.persons.filter(a => a.id !== person.id);
    this.persons = [...newAccounts]
  }
}
