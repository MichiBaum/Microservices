import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  ChessEvent,
  ChessEventCategory,
  Gender,
  Person,
  SearchChessEvent,
  WriteChessEvent
} from "../../core/models/chess/chess.models";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";
import {Fieldset} from "primeng/fieldset";
import {SelectChessEventComponent} from "../select-chess-event/select-chess-event.component";
import {FloatLabel} from "primeng/floatlabel";
import {DatePicker} from "primeng/datepicker";
import {MultiSelect} from "primeng/multiselect";
import {Button} from "primeng/button";
import {InputText} from "primeng/inputtext";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgIf} from "@angular/common";
import {PickList} from "primeng/picklist";
import {LazyLoad} from "../../core/models/lazy-load.model";

@Component({
  selector: 'app-chess-update-event',
  imports: [
    Fieldset,
    SelectChessEventComponent,
    ReactiveFormsModule,
    FloatLabel,
    DatePicker,
    MultiSelect,
    Button,
    InputText,
    FaIconComponent,
    NgIf,
    PickList,
    FormsModule
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
    private readonly chessService: ChessService
  ) { }

  ngOnInit(): void {
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
    this.resetSourceTargetFilter()
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

    const dateFrom = this.getDate(this.formGroup.controls['dateFrom'].value)
    const dateTo = this.getDate(this.formGroup.controls['dateTo'].value)

    const event: WriteChessEvent = {
      title: this.formGroup.controls['title'].value,
      location: this.formGroup.controls['location'].value,
      dateFrom: dateFrom,
      dateTo: dateTo,
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

  getDate(date: Date): string | undefined {
    // TODO this is a quickfix for timezones
    let dateString: string | undefined = undefined;
    if(date != undefined){
      const offset = date.getTimezoneOffset()
      dateString = new Date(date.getTime() - (offset*60*1000)).toISOString().split('T')[0]
    }
    return dateString;
  }

  clear() {
    this.formGroup.reset();
    this.selectedEvent = undefined;
    this.resetParticipantsSelect();
  }

  confirmDelete() {

  }

  getGenderIcon(person: Person) {
    if(person.gender == Gender.MALE)
      return faMars
    if (person.gender == Gender.FEMALE)
      return faVenus
    return faVenusMars;
  }

  lazyLoadEvents(lazyLoad: LazyLoad<SearchChessEvent>) {
    this.chessService.searchEvents(lazyLoad.data).subscribe(newEvents => {
      const filtered = newEvents.filter(event => this.events.every(oldEvent => oldEvent.id !== event.id))
      if(filtered.length != 0){
        this.events = [...this.events, ...filtered];
      }
    })
  }








  // TODO sourceHeader and targetHeader are temporary because f PrimeNGs Filters are f broken
  sourceSearchText: string = "";
  targetSearchText: string = "";
  beforeSourceSearchPersons: Person[] = [];
  beforeTargetSearchPersons: Person[] = [];

  onSourceSearch() {
    if(this.sourceSearchText === ""){
      if(this.beforeSourceSearchPersons.length == 0){
        return;
      }
      this.personsToSelect = [...this.beforeSourceSearchPersons]
      return;
    }

    if(this.beforeSourceSearchPersons.length == 0){
      this.beforeSourceSearchPersons = [...this.personsToSelect]
    }

    const filtered = this.personsToSelect.filter(person =>
      person.firstname.toLowerCase().includes(this.sourceSearchText.toLowerCase()) ||
      person.lastname.toLowerCase().includes(this.sourceSearchText.toLowerCase())
    )
    this.personsToSelect = [...filtered]
  }

  onTargetSearch() {
    if(this.targetSearchText === ""){
      if(this.beforeTargetSearchPersons.length == 0){
        return;
      }
      this.participants = [...this.beforeTargetSearchPersons]
      return;
    }

    if(this.beforeTargetSearchPersons.length == 0){
      this.beforeTargetSearchPersons = [...this.participants]
    }

    const filtered = this.participants.filter(person =>
      person.firstname.toLowerCase().includes(this.targetSearchText.toLowerCase()) ||
      person.lastname.toLowerCase().includes(this.targetSearchText.toLowerCase())
    )
    this.participants = [...filtered]
  }

  resetSourceTargetFilter(){
    this.sourceSearchText = "";
    this.targetSearchText = "";
    this.beforeSourceSearchPersons = [...[]];
    this.beforeTargetSearchPersons = [...[]];
  }

}
