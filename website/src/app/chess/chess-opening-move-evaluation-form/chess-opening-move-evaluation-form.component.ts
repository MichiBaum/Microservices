import {Component, input, signal} from '@angular/core';
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {ChessEngine} from "../../core/models/chess/chess.models";
import {Select} from "primeng/select";
import {NgIf} from "@angular/common";
import {Button} from "primeng/button";

@Component({
  selector: 'app-chess-opening-move-evaluation-form',
    imports: [
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        Select,
        NgIf,
        Button
    ],
  templateUrl: './chess-opening-move-evaluation-form.component.html',
  styleUrl: './chess-opening-move-evaluation-form.component.scss'
})
export class ChessOpeningMoveEvaluationFormComponent {

    availableEngines = input.required<ChessEngine[]>()
    selectedEngine = signal<ChessEngine | undefined>(undefined);

    formGroup: FormGroup = new FormGroup({
        id: new FormControl<string | null>({
            value: '',
            disabled: true
        }),
        depth: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ]),
        evaluation: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ])
    });


    delete() {

    }

    clear() {

    }

    save() {

    }

    canDelete() {
        return false;
    }
}
