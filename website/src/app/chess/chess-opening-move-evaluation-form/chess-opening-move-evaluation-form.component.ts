import {Component, input} from '@angular/core';
import {FloatLabel} from "primeng/floatlabel";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {ChessEngine} from "../../core/models/chess/chess.models";
import {Select} from "primeng/select";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-chess-opening-move-evaluation-form',
    imports: [
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        Select,
        NgIf
    ],
  templateUrl: './chess-opening-move-evaluation-form.component.html',
  styleUrl: './chess-opening-move-evaluation-form.component.scss'
})
export class ChessOpeningMoveEvaluationFormComponent {

    availableEngines = input.required<ChessEngine[]>()
    formGroup: FormGroup = new FormGroup({});

}
