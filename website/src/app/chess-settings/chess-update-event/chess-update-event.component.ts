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
import {ChessEvent, ChessEventCategory, Gender, Person, WriteChessEvent} from "../../core/models/chess/chess.models";
import {PickListModule} from "primeng/picklist";
import {NgIf} from "@angular/common";
import {SelectChessEventComponent} from "../select-chess-event/select-chess-event.component";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";
import {ConfirmationService} from "primeng/api";

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
    PickListModule,
    NgIf,
    SelectChessEventComponent,
    FaIconComponent,
  ],
  templateUrl: './chess-update-event.component.html',
  styleUrl: './chess-update-event.component.scss'
})
export class ChessUpdateEventComponent implements OnInit{

  events: ChessEvent[] = [];
  selectedEvent: ChessEvent | undefined;

  categories: ChessEventCategory[] = [];

  allPersons: Person[] = [];
  personsToSelect: Person[] = [];
  participants: Person[] = [];

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
    location: new FormControl<string | null>({
      value: '',
      disabled: false
    }),
    dateFrom: new FormControl<Date | null>({
      value: null,
      disabled: false
    }, [
      Validators.required,
      Validators.nullValidator
    ]),
    dateTo: new FormControl<Date | null>({
      value: null,
      disabled: false
    }, [
      Validators.required,
      Validators.nullValidator
    ]),
    url: new FormControl<string | null>(null),
    embedUrl: new FormControl<string | null>(null),
    categories: new FormControl<ChessEventCategory[]>({
      value: [],
      disabled: false
    }, [
      Validators.required,
    ]),
  });

  constructor(
    private readonly chessService: ChessService,
    private readonly confirmationService: ConfirmationService
  ) { }

  ngOnInit(): void {
    this.chessService.events().subscribe(events => {
      this.events = [...events];
    })
    this.chessService.eventCategories().subscribe(categories => {
      this.categories = [...categories];
    })
    this.chessService.persons().subscribe(persons => {
      this.allPersons = [...persons];
      this.resetParticipantsSelect()
    })
  }

  onEventSelect(event: ChessEvent | undefined){
    this.selectedEvent = event;
    this.resetParticipantsSelect()
    this.patchForm()
  }

  private resetParticipantsSelect(){
    this.personsToSelect = [...[]]
    this.participants = [...[]]
    if(this.selectedEvent){
      const eventParticipants = this.selectedEvent.participants as Person[];
      this.participants = [...eventParticipants];
      const notParticipating = this.allPersons.filter(person => !eventParticipants?.some(participant => participant.id == person.id))
      this.personsToSelect = [...notParticipating]
    } else {
      this.personsToSelect = [...this.allPersons]
      this.participants = [...[]]
    }
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
      location: this.selectedEvent?.location ?? '',
      dateFrom: dateFrom ?? null,
      dateTo: dateTo ?? null,
      url: this.selectedEvent?.url ?? '',
      embedUrl: this.selectedEvent?.embedUrl ?? '',
      categories: this.selectedEvent?.categories ?? [],
    });
    this.resetParticipantsSelect()
  }

  save() {
    if(this.formGroup.invalid){
      return;
    }
    const event: WriteChessEvent = {
      title: this.formGroup.controls['title'].value,
      location: this.formGroup.controls['location'].value,
      dateFrom: this.formGroup.controls['dateFrom'].value.toISOString().split('T')[0],
      dateTo: this.formGroup.controls['dateTo'].value.toISOString().split('T')[0],
      url: this.formGroup.controls['url'].value,
      embedUrl: this.formGroup.controls['embedUrl'].value,
      categoryIds: (this.formGroup.controls['categories'].value as ChessEventCategory[]).map(value => value.id),
      participantsIds: this.participants.map(value => value.id)
    };

    const id = this.formGroup.controls['id'].value ?? ""
    this.chessService.saveEvent(id, event).subscribe(newEvent => {
      this.clear()
      let isNewEvent = !this.events.some(old => old.id === newEvent.id);
      if (isNewEvent){
        this.events = [...this.events, newEvent]
      } else {
        const newEvents = this.events.map(old => old.id === newEvent.id ? newEvent : old);
        this.events = [...newEvents]
      }
    })
  }

  clear() {
    this.formGroup.reset();
    this.selectedEvent = undefined;
    this.resetParticipantsSelect();
  }

  confirmDelete($event: Event) {
    if (!this.selectedEvent)
      return
    this.confirmationService.confirm({
      target: $event.target as EventTarget,
      message: 'Do you want to delete the Event: ' + this.selectedEvent.title,
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass:"p-button-danger p-button-text",
      rejectButtonStyleClass:"p-button-text p-button-text",
      acceptIcon:"none",
      rejectIcon:"none",

      accept: () => {
        console.log("Yes");
      },
      reject: () => {
        console.log("No");
      }
    });
  }

  getGenderIcon(person: Person) {
    if(person.gender == Gender.MALE)
      return faMars
    if (person.gender == Gender.FEMALE)
      return faVenus
    return faVenusMars;
  }
}
