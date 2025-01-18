import {Component, input, output} from '@angular/core';
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
    visibleChange = output<boolean>({alias: '[visible]'});
    openingMove = input<WriteOpeningMove | undefined>(undefined);

    protected readonly close = close;

    onHide() {
        this.visibleChange.emit(false);
    }
}
