import {Component, inject, signal} from '@angular/core';
import {Fieldset} from "primeng/fieldset";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {SelectChessOpeningComponent} from "../select-chess-opening/select-chess-opening.component";
import {ChessOpening, WriteOpeningMove} from "../../core/models/chess/chess.models";
import {of} from "rxjs";
import {ChessMoveTreeComponent} from "../../chess/chess-move-tree/chess-move-tree.component";
import {ChessOpeningFormComponent} from "../../chess/chess-opening-form/chess-opening-form.component";

@Component({
  selector: 'app-chess-update-opening',
    imports: [
        Fieldset,
        FormsModule,
        ReactiveFormsModule,
        SelectChessOpeningComponent,
        ChessMoveTreeComponent,
        ChessOpeningFormComponent
    ],
  templateUrl: './chess-update-opening.component.html',
  styleUrl: './chess-update-opening.component.css'
})
export class ChessUpdateOpeningComponent {
    chessService = inject(ChessService);

    openings = rxResource({
      stream: () => this.chessService.openings()
    });
    selectedOpening = signal<ChessOpening | undefined>(undefined);
    openingMoves = rxResource({
        params: () => ({openingId: this.selectedOpening()?.id}),
        stream: (params) => {
            const openingId = params.params.openingId
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

    }

    clear() {
		this.formGroup.reset();
    }
}


