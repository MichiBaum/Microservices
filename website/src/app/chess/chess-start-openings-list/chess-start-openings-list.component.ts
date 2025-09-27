import {Component, inject} from '@angular/core';
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {ChessOpeningsListComponent} from "../chess-openings-list/chess-openings-list.component";

@Component({
  selector: 'app-chess-start-openings-list',
    imports: [
        ChessOpeningsListComponent
    ],
  templateUrl: './chess-start-openings-list.component.html',
  styleUrl: './chess-start-openings-list.component.css'
})
export class ChessStartOpeningsListComponent {
    private readonly chessService = inject(ChessService);

    openings = rxResource({
        stream: () =>  this.chessService.startingOpenings()
    })

}


