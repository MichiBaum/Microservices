import {Component, inject} from '@angular/core';
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {RouterLink} from "@angular/router";
import {Timeline} from "primeng/timeline";
import {faArrowUpRightFromSquare} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-chess-start-openings-list',
    imports: [
        RouterLink,
        Timeline,
        FaIconComponent
    ],
  templateUrl: './chess-start-openings-list.component.html',
  styleUrl: './chess-start-openings-list.component.scss'
})
export class ChessStartOpeningsListComponent {
    protected readonly faArrowUpRightFromSquare = faArrowUpRightFromSquare;
    private readonly chessService = inject(ChessService);

    startOpenings = rxResource({
        loader: () =>  this.chessService.startingOpenings()
    })

}
