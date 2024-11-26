import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

@Component({
  selector: 'app-chess-settings',
  standalone: true,
  imports: [],
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
