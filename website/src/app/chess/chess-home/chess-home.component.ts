import { Component } from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {
    ChessRecentUpcomingEventsComponent
} from "../chess-recent-upcoming-events/chess-recent-upcoming-events.component";
import {ChessStartOpeningsListComponent} from "../chess-start-openings-list/chess-start-openings-list.component";
import {TranslatePipe} from "@ngx-translate/core";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-chess-home',
    imports: [
        Button,
        Card,
        ChessRecentUpcomingEventsComponent,
        ChessStartOpeningsListComponent,
        TranslatePipe,
        RouterLink
    ],
  templateUrl: './chess-home.component.html',
  styleUrl: './chess-home.component.scss'
})
export class ChessHomeComponent {

}
