import {Component, inject, signal} from '@angular/core';
import {Button} from "primeng/button";
import {Fieldset} from "primeng/fieldset";
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {SelectChessOpeningComponent} from "../select-chess-opening/select-chess-opening.component";
import {ChessOpening} from "../../core/models/chess/chess.models";
import {of} from "rxjs";
import {ChessMoveTreeComponent} from "../../chess/chess-move-tree/chess-move-tree.component";

@Component({
  selector: 'app-chess-update-opening',
    imports: [
        Button,
        Fieldset,
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        SelectChessOpeningComponent,
        ChessMoveTreeComponent
    ],
  templateUrl: './chess-update-opening.component.html',
  styleUrl: './chess-update-opening.component.scss'
})
export class ChessUpdateOpeningComponent {
    chessService = inject(ChessService);

    openings = rxResource({
      loader: () => this.chessService.openings()
    });
    selectedOpening = signal<ChessOpening | undefined>(undefined);
    openingMoves = rxResource({
        request: () => ({openingId: this.selectedOpening()?.id}),
        loader: (params) => {
            const openingId = params.request.openingId
            if (openingId == undefined)
                return of(undefined)
            return this.chessService.openingMoves(openingId)
        }
    })

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

    setSelectedOpening(opening: ChessOpening | undefined) {
      this.selectedOpening.set(opening)
      this.patchForm()
    }

    patchForm() {
      this.formGroup?.patchValue({
        id: this.selectedOpening()?.id ?? '',
        name: this.selectedOpening()?.name ?? ''
      })
    }

    save() {
	// 	TODO
    }

    clear() {
		this.formGroup.reset();
    }
}
