import {
    Component,
    computed,
    effect,
    inject,
    Input,
    input,
    OnChanges,
    output,
    signal,
    SimpleChanges
} from '@angular/core';
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {ChessEngine, OpeningEvaluation} from "../../core/models/chess/chess.models";
import {Select} from "primeng/select";
import {NgIf} from "@angular/common";
import {Button} from "primeng/button";
import {ChessService} from "../../core/api-services/chess.service";

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
export class ChessOpeningMoveEvaluationFormComponent implements OnChanges {

    chessService = inject(ChessService)

    engines = input.required<ChessEngine[]>()
    evaluation = input<OpeningEvaluation>();

    onDelete = output<void>()
    onSave = output<OpeningEvaluation>()

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
        ]),
        engineId: new FormControl<ChessEngine | null>({
            value: null,
            disabled: false,
        }, [
            Validators.required,
        ]),
    });

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['evaluation']?.currentValue != undefined) {
            const evaluation = changes['evaluation'].currentValue as OpeningEvaluation;
            const engines = changes['engines']?.currentValue as ChessEngine[] ?? [] as ChessEngine[];
            const selectedEngine = engines.find(x => x.id === evaluation.engineId) ?? null;
            this.formGroup.patchValue({
                id: evaluation.id,
                depth: evaluation.depth,
                evaluation: evaluation.evaluation,
                engineId: selectedEngine,
            })
        }
    }

    delete() {
        this.chessService.deleteOpeningEvaluation(this.formGroup.controls['id'].value)
            .subscribe(x => this.onDelete.emit())
    }

    clear() {
        this.formGroup.reset();
    }

    save() {
        this.chessService.
    }

    canDelete() {
        return this.formGroup.controls['id'].value != undefined && this.formGroup.controls['id'].value !== '';
    }
}
