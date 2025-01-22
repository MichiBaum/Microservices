import {Component, inject, OnDestroy, signal} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../../core/api-services/chess.service";
import {of} from "rxjs";
import {ChessMoveTreeComponent} from "../../chess-move-tree/chess-move-tree.component";
import {ChessOpeningActionsComponent} from "../chess-opening-actions/chess-opening-actions.component";
import {SelectedMove} from "../../chess-move-tree/selected-move.model";

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

    moveTreeDepth = signal(6)
    openingMove = rxResource({
        request: () => ({openingId: this.openingId()}),
        loader: (params) => {
            let openingId = params.request.openingId;
            if (openingId == undefined)
                return of(undefined)
            return this.chessService.openingChildrenMoves(this.openingId(), this.moveTreeDepth())
        }
    })

    ngOnDestroy(): void {
        this.routeParamsSubscription.unsubscribe()
    }

    reloadData() {
        this.openingMove.reload()
        this.selectedMove.set(undefined)
    }

    selectedMove = signal<SelectedMove | undefined>(undefined)
    moveSelected(move: SelectedMove | undefined) {
        this.selectedMove.set(move)
    }
}
