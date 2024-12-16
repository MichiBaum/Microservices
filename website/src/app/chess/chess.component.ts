import {Component, OnInit} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MenubarModule} from "primeng/menubar";
import {BadgeModule} from "primeng/badge";
import {TagModule} from "primeng/tag";
import {RouterOutlet} from "@angular/router";
import {ChessNavigationComponent} from "./chess-navigation/chess-navigation.component";

@Component({
  selector: 'app-chess',
  standalone: true,
  imports: [
    SplitterModule,
    MenubarModule,
    BadgeModule,
    TagModule,
    RouterOutlet,
    ChessNavigationComponent
  ],
  templateUrl: './chess.component.html',
  styleUrl: './chess.component.scss'
})
export class ChessComponent implements OnInit{

  constructor(
    private readonly headerService: HeaderService,
  ) { }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.chess.translationKey)
  }

}
