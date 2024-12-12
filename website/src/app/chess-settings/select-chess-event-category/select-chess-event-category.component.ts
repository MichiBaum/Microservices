import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ChessEventCategory} from "../../core/models/chess/chess.models";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";

@Component({
  selector: 'app-select-chess-event-category',
  standalone: true,
  imports: [
    FormsModule,
    InputTextModule,
    PrimeTemplate,
    TableModule
  ],
  templateUrl: './select-chess-event-category.component.html',
  styleUrl: './select-chess-event-category.component.scss'
})
export class SelectChessEventCategoryComponent {
  @Input() categories: ChessEventCategory[] = [];
  @Output() selectedCategoryEmitter = new EventEmitter<ChessEventCategory>();

  selectedCategory: ChessEventCategory | undefined;
  tableSearch: string = "";

  onSelectionChange() {
    this.selectedCategoryEmitter.emit(this.selectedCategory)
  }
}
