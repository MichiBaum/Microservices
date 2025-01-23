import {Component, input, linkedSignal, output} from '@angular/core';
import {Dialog} from "primeng/dialog";
import {ChessOpeningFormComponent} from "../chess-opening-form/chess-opening-form.component";
import {ChessOpening} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-opening-form-dialog',
    imports: [
        Dialog,
        ChessOpeningFormComponent
    ],
  templateUrl: './chess-opening-form-dialog.component.html',
  styleUrl: './chess-opening-form-dialog.component.scss'
})
export class ChessOpeningFormDialogComponent {

    visible = input<boolean>(false);
    _visible = linkedSignal(() => this.visible())
    opening = input<ChessOpening | undefined>(undefined);
    visibleChange = output<boolean>();
    saved = output<ChessOpening>();
    deleted = output<void>();

    onHide() {
        this.visibleChange.emit(false);
    }

    savedChange(opening: ChessOpening) {
        this.onHide();
        this.saved.emit(opening);
    }

    deletedChange() {
        this.onHide();
        this.deleted.emit();
    }

}
