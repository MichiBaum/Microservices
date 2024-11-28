import {Component, OnInit} from '@angular/core';
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";
import {CalendarModule} from "primeng/calendar";
import {MultiSelectModule} from "primeng/multiselect";
import {ChessService} from "../../core/services/chess.service";
import {ChessEvent, ChessEventCategory} from "../../core/models/chess/chess-event.models";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {PaginatorModule} from "primeng/paginator";
import {DropdownModule} from "primeng/dropdown";

@Component({
  selector: 'app-chess-update-event',
  standalone: true,
  imports: [
    InputGroupAddonModule,
    InputGroupModule,
    InputTextModule,
    CalendarModule,
    MultiSelectModule,
    DropdownModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './chess-update-event.component.html',
  styleUrl: './chess-update-event.component.scss'
})
export class ChessUpdateEventComponent implements OnInit{

  events: ChessEvent[] = [];
  selectedEvent: ChessEvent | undefined;

  categories: ChessEventCategory[] = [];

  formGroup: FormGroup = new FormGroup({
    id: new FormControl<string | null>({
      value: '',
      disabled: true
    }),
    title: new FormControl<string | null>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    dateFrom: new FormControl<Date | null>(null),
    dateTo: new FormControl<Date | null>(null),
    url: new FormControl<string | null>(null),
    embedUrl: new FormControl<string | null>(null),
    categories: new FormControl<ChessEventCategory[] | null>(null),
  });

  constructor(
    private readonly chessService: ChessService
  ) { }

  ngOnInit(): void {
    this.chessService.events().subscribe(events => {
      this.events = [...events];
    })
    this.chessService.eventCategories().subscribe(categories => {
      this.categories = [...categories];
    })
  }

  onDropdownSelect(event: any){
    this.patchForm()
  }

  patchForm(){
    var dateFrom
    if(this.selectedEvent?.dateFrom){
      dateFrom = new Date(this.selectedEvent?.dateFrom)
    }
    var dateTo
    if(this.selectedEvent?.dateTo){
      dateTo = new Date(this.selectedEvent?.dateTo)
    }

    this.formGroup?.patchValue({
      id: this.selectedEvent?.id ?? '',
      title: this.selectedEvent?.title ?? '',
      dateFrom: dateFrom ?? null,
      dateTo: dateTo ?? null,
      url: this.selectedEvent?.url ?? '',
      embedUrl: this.selectedEvent?.embedUrl ?? '',
      categories: this.selectedEvent?.categories ?? [],
    });
  }

  save() {
    if(this.formGroup.invalid){
      return;
    }
    console.log(this.formGroup.getRawValue()) // TODO map and post save request
  }

  clear() {
    this.formGroup.reset();
    this.selectedEvent = undefined;
  }

  delete() {
    console.log(this.formGroup.controls['id'].value) // TODO delete request
  }
}
