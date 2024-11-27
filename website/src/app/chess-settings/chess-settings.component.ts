import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {ChessUpdatePersonComponent} from "./chess-update-person/chess-update-person.component";
import {ChessUpdateEventComponent} from "./chess-update-event/chess-update-event.component";
import {FieldsetModule} from "primeng/fieldset";

@Component({
  selector: 'app-chess-settings',
  standalone: true,
  imports: [
    ChessUpdatePersonComponent,
    ChessUpdateEventComponent,
    FieldsetModule
  ],
  templateUrl: './chess-settings.component.html',
  styleUrl: './chess-settings.component.scss'
})
export class ChessSettingsComponent implements OnInit{

  constructor(
    private readonly headerService: HeaderService
  ) { }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.chess_settings.translationKey)
  }

}
