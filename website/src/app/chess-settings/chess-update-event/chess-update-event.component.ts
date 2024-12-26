import {Component, OnInit, inject, signal} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  ChessEvent,
  ChessEventCategory,
  Person,
  SearchChessEvent,
  WriteChessEvent
} from "../../core/models/chess/chess.models";
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
import {rxResource} from "@angular/core/rxjs-interop";
import {EventIconPipe} from "../../core/pipes/gender-icon.pipe";

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
    FormsModule,
    EventIconPipe
  ],
  templateUrl: './chess-update-event.component.html',
  styleUrl: './chess-update-event.component.scss'
})
export class ChessUpdateEventComponent implements OnInit{
  private readonly chessService = inject(ChessService);


  events = signal<ChessEvent[]>([])
  selectedEvent = signal<ChessEvent | undefined>(undefined)

  categories = rxResource({
    loader: () => this.chessService.eventCategories(),
  })

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


  ngOnInit(): void {
    this.chessService.persons().subscribe(persons => {
      this.allPersons = [...persons];
      this.resetParticipantsSelect()
    })
  }

  onEventSelect(event: ChessEvent | undefined){
    this.selectedEvent.set(event);
    this.resetParticipantsSelect()
    this.patchForm()
  }

  private resetParticipantsSelect(){
    this.personsToSelect = [...[]]
    this.participants = [...[]]
    let selectedEvent = this.selectedEvent();
    if(selectedEvent){
      const eventParticipants = selectedEvent.participants as Person[];
      this.participants = [...eventParticipants];
      const notParticipating = this.allPersons.filter(person => !eventParticipants?.some(participant => participant.id == person.id))
      this.personsToSelect = [...notParticipating]
    } else {
      this.personsToSelect = [...this.allPersons]
      this.participants = [...[]]
    }
  }

  patchForm(){
    const selectedEvent = this.selectedEvent();

    if(selectedEvent == undefined){
      this.clear()
      return;
    }

    let dateFrom;
    if(selectedEvent.dateFrom){
      dateFrom = new Date(selectedEvent.dateFrom)
    }
    let dateTo;
    if(selectedEvent.dateTo){
      dateTo = new Date(selectedEvent.dateTo)
    }

    this.formGroup?.patchValue({
      id: selectedEvent.id ?? '',
      title: selectedEvent.title ?? '',
      location: selectedEvent.location ?? '',
      dateFrom: dateFrom ?? null,
      dateTo: dateTo ?? null,
      url: selectedEvent.url ?? '',
      embedUrl: selectedEvent.embedUrl ?? '',
      categories: selectedEvent.categories ?? [],
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
      let isNewEvent = !this.events().some(old => old.id === newEvent.id);
      if (isNewEvent){
        this.events.update(value => [...value, newEvent])
      } else {
        const newEvents = this.events().map(old => old.id === newEvent.id ? newEvent : old);
        this.events.set(newEvents)
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
    this.selectedEvent.set(undefined);
    this.resetParticipantsSelect();
  }

  confirmDelete() {

  }

  lazyLoadEvents(lazyLoad: LazyLoad<SearchChessEvent>) {
    this.chessService.searchEvents(lazyLoad.data).subscribe(newEvents => {
      const filtered = newEvents.filter(event => this.events().every(oldEvent => oldEvent.id !== event.id))
      if(filtered.length != 0){
        this.events.update(value => [...value, ...filtered])
      }
    })
  }

}
