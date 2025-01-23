import {Component, input, linkedSignal, output} from '@angular/core';
import {ChessOpeningMoveFormComponent} from "../chess-opening-move-form/chess-opening-move-form.component";
import {Dialog} from "primeng/dialog";
import {WriteOpeningMove} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-opening-move-form-dialog',
    imports: [
        ChessOpeningMoveFormComponent,
        Dialog
    ],
  templateUrl: './chess-opening-move-form-dialog.component.html',
  styleUrl: './chess-opening-move-form-dialog.component.scss'
})
export class ChessOpeningMoveFormDialogComponent {
    visible = input<boolean>(false);
    _visible = linkedSignal(() => this.visible())
    openingMove = input<WriteOpeningMove | undefined>(undefined);
    visibleChange = output<boolean>();
    saved = output<WriteOpeningMove>();
    deleted = output<void>();

    hide() {
        this._visible.set(false);
        this.visibleChange.emit(false);
    }

    savedChange(move: WriteOpeningMove) {
        this.hide()
        this.saved.emit(move);
    }

    deletedChange() {
        this.hide()
        this.deleted.emit();
    }
}
