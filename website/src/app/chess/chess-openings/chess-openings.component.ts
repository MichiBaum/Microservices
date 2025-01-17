import {Component, inject} from '@angular/core';
import {RouterLink, RouterOutlet} from "@angular/router";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {NgForOf} from "@angular/common";
import {Button} from "primeng/button";
import {Card} from "primeng/card";

@Component({
  selector: 'app-chess-openings',
    imports: [
        RouterOutlet,
        NgForOf,
        Button,
        Card,
        RouterLink
    ],
  templateUrl: './chess-openings.component.html',
  styleUrl: './chess-openings.component.scss'
})
export class ChessOpeningsComponent {
    private readonly chessService = inject(ChessService);

    startOpenings = rxResource({
        loader: () =>  this.chessService.startingOpenings()
    })

}
