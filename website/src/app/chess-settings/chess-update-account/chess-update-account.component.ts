import {Component, inject, signal} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/api-services/chess.service";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {Button, ButtonDirective} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {Account, ChessPlatform} from "../../core/models/chess/chess.models";
import {TableModule} from "primeng/table";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {CheckboxModule} from "primeng/checkbox";
import {NgIf} from "@angular/common";

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
    NgIf
  ],
  templateUrl: './chess-update-account.component.html',
  styleUrl: './chess-update-account.component.css'
})
export class ChessUpdateAccountComponent{
  private readonly chessService = inject(ChessService);
  private readonly routerService = inject(RouterNavigationService);

  username = signal("");
  localSearch = signal(true);

  importedAccounts = signal<Account[]>([]);

  search(){
    this.chessService.accountsSearch(this.username(), this.localSearch()).subscribe(accounts => {
      this.importedAccounts.set(accounts)
    })
  }

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

}


