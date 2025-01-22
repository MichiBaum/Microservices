import {Component, input, linkedSignal, output} from '@angular/core';
import {
    ChessOpeningMoveEvaluationFormComponent
} from "../chess-opening-move-evaluation-form/chess-opening-move-evaluation-form.component";
import {WriteOpeningMove} from "../../core/models/chess/chess.models";
import {Dialog} from "primeng/dialog";
import {SelectedMove} from "../chess-move-tree/selected-move.model";

@Component({
  selector: 'app-chess-opening-move-evaluation-form-dialog',
    imports: [
        ChessOpeningMoveEvaluationFormComponent,
        Dialog
    ],
  templateUrl: './chess-opening-move-evaluation-form-dialog.component.html',
  styleUrl: './chess-opening-move-evaluation-form-dialog.component.scss'
})
export class ChessOpeningMoveEvaluationFormDialogComponent {

    visible = input<boolean>(false);
    _visible = linkedSignal(() => this.visible())
    openingMove = input<SelectedMove | undefined>(undefined);
    visibleChange = output<boolean>();
    saved = output<WriteOpeningMove>();

    hide() {
        this._visible.set(false);
        this.visibleChange.emit(false);
    }

}
