import {Component, input, output, signal} from '@angular/core';
import {TableModule} from "primeng/table";
import {ChessOpening} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-select-chess-opening',
  imports: [
    TableModule
  ],
  templateUrl: './select-chess-opening.component.html',
  styleUrl: './select-chess-opening.component.css'
})
export class SelectChessOpeningComponent {
  openings = input<ChessOpening[]>([]);
  tableSelectedOpening = signal<ChessOpening | undefined>(undefined);

  selectedOpening = output<ChessOpening | undefined>();

  onSelectionChange() {
    this.selectedOpening.emit(this.tableSelectedOpening())
  }
}


