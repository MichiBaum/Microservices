import {Component, input} from '@angular/core';
import {Timeline} from "primeng/timeline";
import {RouterLink} from "@angular/router";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowUpRightFromSquare} from "@fortawesome/free-solid-svg-icons";
import {ChessOpening} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-openings-list',
    imports: [
        Timeline,
        RouterLink,
        FaIconComponent
    ],
  templateUrl: './chess-openings-list.component.html',
  styleUrl: './chess-openings-list.component.css'
})
export class ChessOpeningsListComponent {
    protected readonly faArrowUpRightFromSquare = faArrowUpRightFromSquare;

    openings = input.required<ChessOpening[]>()

}


