import {Component, input, output, signal} from '@angular/core';
import {ChessEventCategory} from "../../core/models/chess/chess.models";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {IconField} from "primeng/iconfield";
import {InputIcon} from "primeng/inputicon";

@Component({
  selector: 'app-select-chess-event-category',
  imports: [
    FormsModule,
    InputTextModule,
    PrimeTemplate,
    TableModule,
    IconField,
    InputIcon
  ],
  templateUrl: './select-chess-event-category.component.html',
  styleUrl: './select-chess-event-category.component.scss'
})
export class SelectChessEventCategoryComponent {
  readonly categories = input<ChessEventCategory[]>([]);
  readonly selectedCategoryEmitter = output<ChessEventCategory | undefined>();

  selectedCategory = signal<ChessEventCategory | undefined>(undefined);
  tableSearch: string = "";

  onSelectionChange() {
    this.selectedCategoryEmitter.emit(this.selectedCategory())
  }
}
