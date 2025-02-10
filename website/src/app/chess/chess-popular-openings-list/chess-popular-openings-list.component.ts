import {Component, inject} from '@angular/core';
import {ChessOpeningsListComponent} from "../chess-openings-list/chess-openings-list.component";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";

@Component({
  selector: 'app-chess-popular-openings-list',
    imports: [
        ChessOpeningsListComponent
    ],
  templateUrl: './chess-popular-openings-list.component.html',
  styleUrl: './chess-popular-openings-list.component.scss'
})
export class ChessPopularOpeningsListComponent {
    private readonly chessService = inject(ChessService);

    openings = rxResource({
        loader: () =>  this.chessService.popularOpenings()
    })

}
