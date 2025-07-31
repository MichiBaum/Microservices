import {Component, inject, input} from '@angular/core';
import {Card} from "primeng/card";
import {Button} from "primeng/button";
import {Account, Person} from "../../../core/models/chess/chess.models";
import {TranslatePipe} from "@ngx-translate/core";

import {RouterNavigationService} from "../../../core/services/router-navigation.service";
import {EventIconPipe} from "../../../core/pipes/gender-icon.pipe";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-chess-player-card',
  imports: [
    Card,
    Button,
    TranslatePipe,
    EventIconPipe,
    FaIconComponent
],
  templateUrl: './chess-player-card.component.html',
  styleUrl: './chess-player-card.component.css'
})
export class ChessPlayerCardComponent {
  private readonly navigationService = inject(RouterNavigationService);

  player = input<Person>()
  accounts = input<Account[]>();

  openAccount(account: Account) {
    this.navigationService.open(account.url)
  }

  protected readonly length = length;
}


