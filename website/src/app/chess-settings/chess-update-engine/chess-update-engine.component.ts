import {Component, inject, signal} from '@angular/core';
import {SelectChessEngineComponent} from "../select-chess-engine/select-chess-engine.component";
import {rxResource} from "@angular/core/rxjs-interop";
import {ChessService} from "../../core/api-services/chess.service";
import {ChessEngine, WriteChessEngine} from "../../core/models/chess/chess.models";
import {JsonPipe} from "@angular/common";
import {Fieldset} from "primeng/fieldset";
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {Button} from "primeng/button";

@Component({
  selector: 'app-chess-update-engine',
  imports: [
    SelectChessEngineComponent,
    JsonPipe,
    Fieldset,
    FloatLabel,
    FormsModule,
    InputText,
    ReactiveFormsModule,
    Button
  ],
  templateUrl: './chess-update-engine.component.html',
  styleUrl: './chess-update-engine.component.scss'
})
export class ChessUpdateEngineComponent {
  private readonly chessService = inject(ChessService);

  engines = rxResource({
    loader:() => this.chessService.engines(),
  })
  selectedEngine = signal<ChessEngine | undefined>(undefined);

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
    ]),
    version: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ])
  })

  setSelectedEngine(engine: ChessEngine | undefined) {
    this.selectedEngine.set(engine)
    this.patchForm()
  }

  patchForm() {
    const selectedEngine = this.selectedEngine();
    if(selectedEngine == undefined){
      this.clear();
      return;
    }

    this.formGroup?.patchValue({
      id: selectedEngine.id ?? '',
      name: selectedEngine.name ?? '',
      version: selectedEngine.version ?? ''
    })
  }

  save() {
    if(this.formGroup.invalid)
      return;

    const engine: WriteChessEngine = {
      name: this.formGroup.controls['name'].value,
      version: this.formGroup.controls['version'].value
    }

    const id = this.formGroup.controls['id'].value ?? ""

    if(id == undefined || id == ""){
      this.chessService.createEngine(engine).subscribe( _ => {
          this.clear()
          this.engines.reload()
        }
      )
    } else {
      this.chessService.updateEngine(id, engine).subscribe( _ => {
          this.clear()
          this.engines.reload()
        }
      )
    }

  }

  clear() {
    this.formGroup.reset();
  }
}
