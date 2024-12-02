import {Component, OnInit} from '@angular/core';
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";
import {CalendarModule} from "primeng/calendar";
import {MultiSelectModule} from "primeng/multiselect";
import {ChessService} from "../../core/services/chess.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {FieldsetModule} from "primeng/fieldset";
import {ListboxModule} from "primeng/listbox";
import {TableModule} from "primeng/table";
import {Ripple} from "primeng/ripple";
import {ChessEvent, ChessEventCategory, Person} from "../../core/models/chess/chess.models";
import {PickListModule} from "primeng/picklist";
import {NgIf} from "@angular/common";
import {SelectChessEventComponent} from "../select-chess-event/select-chess-event.component";

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
    FieldsetModule,
    ListboxModule,
    TableModule,
    Ripple,
    PickListModule,
    NgIf,
    SelectChessEventComponent,
  ],
  templateUrl: './chess-update-event.component.html',
  styleUrl: './chess-update-event.component.scss'
})
export class ChessUpdateEventComponent implements OnInit{

  events: ChessEvent[] = [];
  selectedEvent: ChessEvent | undefined;

  categories: ChessEventCategory[] = [];

  persons: Person[] = [];
  participants: Person[] = [];

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

  onEventSelect(event: ChessEvent){
    this.selectedEvent = event;
    this.loadPersons(event)
    this.patchForm()
  }

  private loadPersons(event: ChessEvent) {
    const eventParticipants = event.participants as Person[];
    this.participants = [...eventParticipants];
    this.chessService.persons().subscribe(
      persons => {
        const notParticipating = persons.filter(person => !eventParticipants?.some(participant => participant.id == person.id))
        this.persons = [...notParticipating]
      })
  }

  patchForm(){
    let dateFrom;
    if(this.selectedEvent?.dateFrom){
      dateFrom = new Date(this.selectedEvent?.dateFrom)
    }
    let dateTo;
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
    this.participants = [...[]];
    this.persons = [...[]];
  }

  delete() {
    console.log(this.formGroup.controls['id'].value) // TODO delete request
  }
}
