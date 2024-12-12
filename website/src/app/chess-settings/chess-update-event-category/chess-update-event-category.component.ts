import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessEventCategoryComponent} from "../select-chess-event-category/select-chess-event-category.component";
import {ChessEventCategory} from "../../core/models/chess/chess.models";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";

@Component({
  selector: 'app-chess-update-event-category',
  standalone: true,
  imports: [
    FieldsetModule,
    SelectChessEventCategoryComponent,
    FormsModule,
    InputGroupAddonModule,
    InputGroupModule,
    InputTextModule,
    ReactiveFormsModule
  ],
  templateUrl: './chess-update-event-category.component.html',
  styleUrl: './chess-update-event-category.component.scss'
})
export class ChessUpdateEventCategoryComponent implements OnInit{

  categories: ChessEventCategory[] = [];
  selectedCategory: ChessEventCategory | undefined;

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

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.eventCategories().subscribe(
      eventCategories => this.categories = [...eventCategories]
    )
  }

  onCategorySelect(category: ChessEventCategory | undefined) {
    this.selectedCategory = category;
    this.patchForm()
  }

  private patchForm() {
    this.formGroup?.patchValue({
      id: this.selectedCategory?.id ?? '',
      title: this.selectedCategory?.title ?? '',
      description: this.selectedCategory?.description ?? '',
    })
  }
}
