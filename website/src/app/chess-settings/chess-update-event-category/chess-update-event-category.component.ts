import {Component, inject, signal} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessEventCategoryComponent} from "../select-chess-event-category/select-chess-event-category.component";
import {ChessEventCategory, WriteChessEventCategory} from "../../core/models/chess/chess.models";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";
import {Button} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {rxResource} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-chess-update-event-category',
  imports: [
    FieldsetModule,
    SelectChessEventCategoryComponent,
    FormsModule,
    InputGroupAddonModule,
    InputGroupModule,
    InputTextModule,
    ReactiveFormsModule,
    Button,
    FloatLabel
  ],
  templateUrl: './chess-update-event-category.component.html',
  styleUrl: './chess-update-event-category.component.scss'
})
export class ChessUpdateEventCategoryComponent {
  private readonly chessService = inject(ChessService);

  categories = rxResource({
    loader: () => this.chessService.eventCategories(),
  })
  selectedCategory = signal<ChessEventCategory | undefined>(undefined)

  formGroup: FormGroup = new FormGroup({
    id: new FormControl<string | null>({
      value: '',
      disabled: true
    }),
    title: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    description: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
  })

  onCategorySelect(category: ChessEventCategory | undefined) {
    this.selectedCategory.set(category);
    this.patchForm(category)
  }

  private patchForm(selectedCategory: ChessEventCategory | undefined) {
    this.formGroup?.patchValue({
      id: selectedCategory?.id ?? '',
      title: selectedCategory?.title ?? '',
      description: selectedCategory?.description ?? '',
    })
  }

  save() {
    if (this.formGroup.invalid) {
      return;
    }

    const category: WriteChessEventCategory = {
      title: this.formGroup.controls['title'].value,
      description: this.formGroup.controls['description'].value
    }

    const id = this.formGroup.controls['id'].value ?? ""
    this.chessService.saveCategory(id, category).subscribe(_ => {
      this.clear()
      this.categories.reload()
    })

  }

  clear() {
    this.formGroup.reset();
    this.selectedCategory.set(undefined);
  }

  confirmDelete() {

  }
}
