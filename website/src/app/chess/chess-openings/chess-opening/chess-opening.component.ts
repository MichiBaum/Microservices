import {Component, inject, linkedSignal, OnDestroy, signal} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../../core/api-services/chess.service";
import {of} from "rxjs";
import {ChessMoveTreeComponent} from "../../chess-move-tree/chess-move-tree.component";
import {ChessOpeningActionsComponent} from "../chess-opening-actions/chess-opening-actions.component";
import {SelectedMove} from "../../chess-move-tree/selected-move.model";
import {FormsModule} from "@angular/forms";
import {InputNumber} from "primeng/inputnumber";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faMinus, faPlus} from "@fortawesome/free-solid-svg-icons";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-chess-opening',
    imports: [
        ChessMoveTreeComponent,
        ChessOpeningActionsComponent,
        FormsModule,
        InputNumber,
        FaIconComponent,
        TranslatePipe
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

    moveTreeDepth = linkedSignal<number>(() => 6)
    openingMove = rxResource({
        request: () => ({openingId: this.openingId(), depth: this.moveTreeDepth()}),
        loader: (params) => {
            let openingId = params.request.openingId;
            if (openingId == undefined)
                return of(undefined)
            return this.chessService.openingChildrenMoves(openingId, params.request.depth)
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

    protected readonly faPlus = faPlus;
    protected readonly faMinus = faMinus;
}
