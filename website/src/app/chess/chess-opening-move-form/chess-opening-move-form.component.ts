import {Component, inject, input, OnChanges, output, SimpleChanges} from '@angular/core';
import {WriteOpeningMove} from "../../core/models/chess/chess.models";
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {Button} from "primeng/button";
import {ChessService} from "../../core/api-services/chess.service";

@Component({
    selector: 'app-chess-opening-move-form',
    imports: [
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        Button
    ],
    templateUrl: './chess-opening-move-form.component.html',
    styleUrl: './chess-opening-move-form.component.scss'
})
export class ChessOpeningMoveFormComponent implements OnChanges {
    chessService = inject(ChessService)

    openingMove = input<WriteOpeningMove | undefined>()

    saved = output<WriteOpeningMove>()
    onClear = output<void>()

    formGroup: FormGroup = new FormGroup({
        id: new FormControl<string | null>({
            value: '',
            disabled: true
        }),
        move: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ])
    })

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['openingMove'].currentValue == undefined){
            this.formGroup.reset();
        }
        if(changes['openingMove'].currentValue != undefined){
            const openingMove = changes['openingMove'].currentValue as WriteOpeningMove;

            this.formGroup?.patchValue({
                id: openingMove.id ?? '',
                move: openingMove.move ?? ''
            })
        }
    }

    save() {
        const newMove: WriteOpeningMove = {
            id: this.formGroup.controls['id'].value ?? '',
            move: this.formGroup.controls['move'].value ?? '',
            parentMoveId: this.openingMove()?.parentMoveId ?? '',
        }
        this.chessService.openingMove(newMove).subscribe( savedMove => {
            this.saved.emit(savedMove)
            this.clear()
        })
    }

    clear() {
        this.formGroup.reset();
        this.onClear.emit()
    }

    delete() {

    }

    canDelete() {
        let move = this.openingMove();
        return move != undefined && move.id != undefined && move.id != '';
    }
}
