import { Component } from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {
    ChessRecentUpcomingEventsComponent
} from "../chess-recent-upcoming-events/chess-recent-upcoming-events.component";
import {ChessStartOpeningsListComponent} from "../chess-start-openings-list/chess-start-openings-list.component";
import {TranslatePipe} from "@ngx-translate/core";
import {RouterLink} from "@angular/router";
import {ChessPopularOpeningsListComponent} from "../chess-popular-openings-list/chess-popular-openings-list.component";

@Component({
  selector: 'app-chess-home',
    imports: [
        Button,
        Card,
        ChessRecentUpcomingEventsComponent,
        ChessStartOpeningsListComponent,
        TranslatePipe,
        RouterLink,
        ChessPopularOpeningsListComponent
    ],
  templateUrl: './chess-home.component.html',
  styleUrl: './chess-home.component.css'
})
export class ChessHomeComponent {

}


