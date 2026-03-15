import {Component, inject, input, output} from '@angular/core';
import {TableModule} from "primeng/table";
import {Account} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";
import {Button} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {RouterNavigationService} from "../../core/services/router-navigation.service";

@Component({
  selector: 'app-select-chess-account',
  imports: [
    TableModule,
    InputTextModule,
    FormsModule,
    FaIconComponent,
    Button,
    InputGroupModule,
  ],
  templateUrl: './select-chess-account.component.html',
  styleUrl: './select-chess-account.component.css'
})
export class SelectChessAccountComponent {
  private readonly routerService = inject(RouterNavigationService);

  readonly accounts = input<Account[]>([]);
  readonly selectedAccount = input<Account | undefined>();
  readonly selectedAccountChange = output<Account | undefined>();


  protected readonly faCheck = faCheck;
  protected readonly faXmark = faXmark;

  open(url: string) {
    this.routerService.open(url);
  }
}
