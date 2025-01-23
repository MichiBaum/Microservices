import {Component, input, output, signal} from '@angular/core';
import {TableModule} from "primeng/table";
import {ChessEngine} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-select-chess-engine',
  imports: [
    TableModule
  ],
  templateUrl: './select-chess-engine.component.html',
  styleUrl: './select-chess-engine.component.scss'
})
export class SelectChessEngineComponent {

  readonly engines = input<ChessEngine[]>([])
  selectedEngine = output<ChessEngine | undefined>()

  tableSelectedEngine = signal<ChessEngine | undefined>(undefined)

  onSelectionChange() {
    this.selectedEngine.emit(this.tableSelectedEngine())
  }
}
