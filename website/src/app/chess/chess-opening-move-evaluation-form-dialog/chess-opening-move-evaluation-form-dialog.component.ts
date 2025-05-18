import {Component, inject, input, linkedSignal, output, signal} from '@angular/core';
import {
    ChessOpeningMoveEvaluationFormComponent
} from "../chess-opening-move-evaluation-form/chess-opening-move-evaluation-form.component";
import {Dialog} from "primeng/dialog";
import {SelectedMove} from "../chess-move-tree/selected-move.model";
import {ChessService} from "../../core/api-services/chess.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {Card} from "primeng/card";
import {NgForOf} from "@angular/common";
import {of} from "rxjs";

@Component({
  selector: 'app-chess-opening-move-evaluation-form-dialog',
    imports: [
        ChessOpeningMoveEvaluationFormComponent,
        Dialog,
        Card,
        NgForOf
    ],
  templateUrl: './chess-opening-move-evaluation-form-dialog.component.html',
  styleUrl: './chess-opening-move-evaluation-form-dialog.component.css'
})
export class ChessOpeningMoveEvaluationFormDialogComponent {
    private readonly chessService = inject(ChessService);

    visible = input<boolean>(false);
    _visible = linkedSignal(() => this.visible())
    openingMove = input.required<SelectedMove>();
    visibleChange = output<boolean>();

    availableEngines = rxResource({
        loader: () => this.chessService.engines()
    })
    evaluations = rxResource({
        request: () => ({moveId: this.openingMove()?.id}),
        loader: (params) => {
            const moveId = params.request.moveId
            if(moveId == undefined) return of([])
            return this.chessService.openingEvaluations(moveId)
        }
    })

    hide() {
        this._visible.set(false);
        this.visibleChange.emit(false);
    }

    savedChange() {
        this.evaluations.reload()
    }

    deletedChange() {
        this.evaluations.reload()
    }
}


