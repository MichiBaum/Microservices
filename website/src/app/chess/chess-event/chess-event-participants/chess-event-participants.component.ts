import {Component, computed, effect, inject, input, signal} from '@angular/core';
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {TranslateModule} from "@ngx-translate/core";
import {Account, ChessEvent, Person} from "../../../core/models/chess/chess.models";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {ChessService} from "../../../core/services/chess.service";
import {ChessPlayerCardComponent} from "../chess-player-card/chess-player-card.component";
import {SelectButton} from "primeng/selectbutton";
import {FormsModule} from "@angular/forms";
import {RadioButton} from "primeng/radiobutton";
import {Fieldset} from "primeng/fieldset";

@Component({
  selector: 'app-chess-event-participants',
  imports: [
    CardModule,
    NgIf,
    TranslateModule,
    NgForOf,
    ChessPlayerCardComponent,
    SelectButton,
    FormsModule,
    RadioButton,
    Fieldset
  ],
  templateUrl: './chess-event-participants.component.html',
  styleUrl: './chess-event-participants.component.scss'
})
export class ChessEventParticipantsComponent {
  private readonly chessService = inject(ChessService);

  readonly event = input<ChessEvent>();
  protected readonly participants = rxResource({
    request: () => ({eventId: this.event()?.id}),
    loader: (params) => {
      let eventId = params.request.eventId;
      if(eventId == undefined)
        return of([])
      return this.chessService.eventParticipants(eventId)
    }
  })

  sortedParticipants = computed(() => {
    const _participants = this.participants.value() ?? []
    const sortBy = this.selectedSort()
    return this.sortParticipants(_participants, sortBy)
  })

  sortOptions: SortBy[] = [
    {name: 'Firstname', value: SortByValue.firstname},
    {name: 'Lastname', value: SortByValue.lastname},
    {name: 'Federation', value: SortByValue.federation},
    {name: 'Birthday', value: SortByValue.birthday}
  ];
  selectedSort = signal<SortByValue>(SortByValue.lastname);

  personsPlatformAccount(participant: Person): Account[] {
    const eventPlatform = this.event()?.platform
    if(eventPlatform == undefined)
      return []
    return participant.accounts.filter(value => value.platform == eventPlatform)
  }

  sortParticipants(_participants: Person[], value: SortByValue) {
    switch (value) {
      case SortByValue.federation:
        return _participants.sort((a, b) => (a.federation || '').localeCompare(b.federation || ''))
      case SortByValue.firstname:
        return _participants.sort((a, b) => a.firstname.localeCompare(b.firstname))
      case SortByValue.lastname:
        return _participants.sort((a, b) => a.lastname.localeCompare(b.lastname))
      case SortByValue.birthday:
        return _participants.sort((a, b) => {
          const dateA = a.birthday ? new Date(a.birthday).getTime() : 0;
          const dateB = b.birthday ? new Date(b.birthday).getTime() : 0;
          return dateB - dateA;
        })
      default:
        return _participants;
    }
  }

}

interface SortBy {
  name: string;
  value: SortByValue;
}
enum SortByValue {
  firstname = 'firstname',
  lastname = 'lastname',
  federation = 'federation',
  birthday = 'birthday'
}
