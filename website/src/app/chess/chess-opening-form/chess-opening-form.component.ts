import {Component, input, OnChanges, output, SimpleChanges} from '@angular/core';
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {Button} from "primeng/button";
import {ChessOpening} from "../../core/models/chess/chess.models";

@Component({
  selector: 'app-chess-opening-form',
    imports: [
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        Button
    ],
  templateUrl: './chess-opening-form.component.html',
  styleUrl: './chess-opening-form.component.scss'
})
export class ChessOpeningFormComponent implements OnChanges{

    opening = input<ChessOpening | undefined>(undefined)
    onSave = output<ChessOpening>()
    onClear = output<void>()

    formGroup: FormGroup = new FormGroup({
        id: new FormControl<string | null>({
            value: '',
            disabled: true
        }),
        name: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ])
    })

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['opening'].currentValue == undefined){
            this.formGroup.reset();
        }
        if(changes['opening'].currentValue != undefined){
            const newOpening = changes['opening'].currentValue as ChessOpening;

            this.formGroup?.patchValue({
                id: newOpening.id ?? '',
                name: newOpening.name ?? ''
            })
        }
    }

    save() {
        //this.onSave.emit()
    }

    clear() {
        this.formGroup.reset();
        this.onClear.emit()
    }

}
