import {Component, effect, inject, output, signal} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {Account, SearchLocation} from "../../core/models/chess/chess.models";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {FormsModule} from "@angular/forms";
import {InputGroup} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";
import {SelectButton} from "primeng/selectbutton";
import {Button} from "primeng/button";

@Component({
  selector: 'app-search-chess-account',
  standalone: true,
  imports: [
    FormsModule,
    InputGroup,
    InputTextModule,
    SelectButton,
    Button
  ],
  templateUrl: './search-chess-account.component.html',
  styleUrl: './search-chess-account.component.css'
})
export class SearchChessAccountComponent {
  private readonly chessService = inject(ChessService);

  searchTerm = signal('');
  searchLocation = signal<SearchLocation>(SearchLocation.LOCAL);

  accounts = rxResource({
    stream: () => {
      const searchTerm = this.searchTerm()
      const localSearch = this.searchLocation()
      if (searchTerm === undefined || searchTerm.length < 1)
        return of([]);
      return this.chessService.accountsSearch(searchTerm, localSearch)
    },
    defaultValue: []
  })

  accountsFound = output<Account[]>();

  constructor() {
    effect(() => {
      this.accountsFound.emit(this.accounts.value())
    });
  }

  reload() {
    this.accounts.reload()
  }

  protected searchLocationOptions(): SearchLocation[] {
    return Object.values(SearchLocation);
  }
}
