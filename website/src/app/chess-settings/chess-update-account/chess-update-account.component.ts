import {Component, OnInit} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/services/chess.service";
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
  standalone: true,
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
  styleUrl: './chess-update-account.component.scss'
})
export class ChessUpdateAccountComponent implements OnInit{
  username: string = '';
  localSearch: boolean = true;

  importedAccounts: Account[] = []

  constructor(
    private readonly chessService: ChessService,
    private readonly routerService: RouterNavigationService
  ) { }

  ngOnInit(): void {
  }

  search(){
    this.chessService.accountsSearch(this.username, this.localSearch).subscribe(accounts => {
      this.importedAccounts = [...accounts]
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
    let isOverTheBoard = account.platform === ChessPlatform.OVER_THE_BOARD;
    return !isOverTheBoard
  }

  delete(id: string) {

  }

}