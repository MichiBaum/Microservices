import {Component, effect, inject, linkedSignal, OnInit, signal} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {
    ChessEvent,
    ChessEventCategory,
    ChessPlatform,
    Person,
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

import {PickList, PickListMoveToSourceEvent, PickListMoveToTargetEvent} from "primeng/picklist";
import {rxResource} from "@angular/core/rxjs-interop";
import {EventIconPipe} from "../../core/pipes/gender-icon.pipe";
import {Textarea} from "primeng/textarea";
import {of} from "rxjs";
import {Select} from "primeng/select";

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
    PickList,
    FormsModule,
    EventIconPipe,
    Textarea,
    Select
],
  templateUrl: './chess-update-event.component.html',
  styleUrl: './chess-update-event.component.css'
})
export class ChessUpdateEventComponent {
  private readonly chessService = inject(ChessService);

  events = rxResource({
    stream: () => this.chessService.events(),
    defaultValue: []
  })
  selectedEvent = signal<ChessEvent | undefined>(undefined)
  preselectedParticipants = rxResource({
    params: () => ({eventId: this.selectedEvent()?.id}),
    stream: (params) => {
      const eventId = params.params.eventId
      if (eventId == undefined)
        return of([])
      return this.chessService.eventParticipants(eventId)
    },
    defaultValue: []
  })

  categories = rxResource({
    stream: () => this.chessService.eventCategories(),
    defaultValue: []
  })

  allPersons = rxResource({
    stream: () => this.chessService.persons(),
    defaultValue: []
  });
  selectedParticipants = signal<Person[]>([])
  personsToSelect = linkedSignal(() => {
    const eventParticipants = this.preselectedParticipants.value() ?? [];
    return this.allPersons.value().filter(person => !eventParticipants?.some(participant => participant.id == person.id))
  })

  platforms = [
    ChessPlatform.FIDE,
    ChessPlatform.CHESSCOM,
    ChessPlatform.FREESTYLE,
    ChessPlatform.LICHESS
  ]

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
    internalComment: new FormControl<string>(''),
    platform: new FormControl<ChessPlatform | null>(
      {
        value: null,
        disabled: false
      }, [
        Validators.required,
        Validators.nullValidator
      ]
    ),
    categories: new FormControl<ChessEventCategory[]>({
      value: [],
      disabled: false
    }, [
      Validators.required,
    ]),
  });

  constructor() {
    effect(() => {
      const selectedEvent = this.selectedEvent();
      this.patchForm(selectedEvent);
    });
    effect(() => {
      const preselectedParticipants = this.preselectedParticipants.value();
      this.selectedParticipants.set(preselectedParticipants);
    })
  }

  patchForm(selectedEvent: ChessEvent | undefined){
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
      internalComment: selectedEvent.internalComment ?? '',
      platform: selectedEvent.platform ?? null,
      embedUrl: selectedEvent.embedUrl ?? '',
      categories: selectedEvent.categories ?? [],
    });
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
      internalComment: this.formGroup.controls['internalComment'].value,
      platform: this.formGroup.controls['platform'].value,
      categoryIds: (this.formGroup.controls['categories'].value as ChessEventCategory[]).map(value => value.id),
      participantsIds: this.selectedParticipants().map(value => value.id)
    };

    const id = this.formGroup.controls['id'].value ?? ""
    this.chessService.saveEvent(id, event).subscribe(newEvent => {
      this.clear()
      this.events.reload()
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
  }

  confirmDelete() {

  }

  protected addParticipant(event: PickListMoveToTargetEvent) {
    const items = event.items as Person[];
    this.selectedParticipants().push(...items)
  }

  protected removeParticipant(event: PickListMoveToSourceEvent) {
    const items = event.items as Person[];
    this.selectedParticipants.update(value => value.filter(person => !items.some(item => item.id == person.id)))
  }
}


