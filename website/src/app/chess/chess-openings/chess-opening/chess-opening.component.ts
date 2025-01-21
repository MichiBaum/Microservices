import {Component, inject, OnDestroy, signal} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../../core/api-services/chess.service";
import {of} from "rxjs";
import {ChessMoveTreeComponent} from "../../chess-move-tree/chess-move-tree.component";
import {ChessOpeningActionsComponent} from "../chess-opening-actions/chess-opening-actions.component";
import {ChessOpeningMove} from "../../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-opening',
    imports: [
        ChessMoveTreeComponent,
        ChessOpeningActionsComponent
    ],
  templateUrl: './chess-opening.component.html',
  styleUrl: './chess-opening.component.scss'
})
export class ChessOpeningComponent implements OnDestroy{
    private readonly route = inject(ActivatedRoute);
    private readonly chessService = inject(ChessService);

    openingId = signal<string>("")

    routeParamsSubscription = this.route.params.subscribe(params => {
        const id = params['id'];
        this.openingId.set(id)
    });

    openingMove = rxResource({
        request: () => ({openingId: this.openingId()}),
        loader: (params) => {
            let openingId = params.request.openingId;
            if (openingId == undefined)
                return of(undefined)
            return this.chessService.openingChildrenMoves(this.openingId())
        }
    })

    ngOnDestroy(): void {
        this.routeParamsSubscription.unsubscribe()
    }

    reloadData() {
        this.openingMove.reload()
        this.selectedMove.set(undefined)
    }

    selectedMove = signal<ChessOpeningMove | undefined>(undefined)
    moveSelected(move: ChessOpeningMove | undefined) {
        this.selectedMove.set(move)
    }
}
