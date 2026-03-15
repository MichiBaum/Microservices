import {Component, inject, signal} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/api-services/chess.service";
import {InputTextModule} from "primeng/inputtext";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Button, ButtonDirective} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {Account, ChessPlatform, SearchLocation} from "../../core/models/chess/chess.models";
import {TableModule} from "primeng/table";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {CheckboxModule} from "primeng/checkbox";
import {FloatLabel} from "primeng/floatlabel";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {SelectButton} from "primeng/selectbutton";

@Component({
  selector: 'app-chess-update-account',
  imports: [
    FieldsetModule,
    InputTextModule,
    FormsModule,
    Button,
    InputGroupModule,
    ButtonDirective,
    TableModule,
    InputGroupAddonModule,
    CheckboxModule,
    FloatLabel,
    ReactiveFormsModule,
    SelectButton
  ],
  templateUrl: './chess-update-account.component.html',
  styleUrl: './chess-update-account.component.css'
})
export class ChessUpdateAccountComponent{
  private readonly chessService = inject(ChessService);
  private readonly routerService = inject(RouterNavigationService);

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

  formGroup: FormGroup = new FormGroup({
    id: new FormControl<string | null>({
      value: '',
      disabled: true
    })
  })

  open(url: string) {
    this.routerService.open(url)
  }

  importGames(id: string) {
    this.chessService.importGames(id).subscribe(nothing => {

    })
  }

  showImportGameButton(account: Account) {
    let isOverTheBoard = account.platform === ChessPlatform.FIDE;
    return !isOverTheBoard
  }

  delete(id: string) {
    // TODO
  }

  protected searchLocationOptions(): SearchLocation[] {
    return Object.values(SearchLocation);
  }
}


