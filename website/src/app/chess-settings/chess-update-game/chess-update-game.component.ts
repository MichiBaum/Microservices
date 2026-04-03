import {Component, computed, effect, inject, signal, untracked} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/api-services/chess.service";
import {Account, ChessEvent, ChessGame, Person} from "../../core/models/chess/chess.models";
import {MultiSelectModule} from "primeng/multiselect";
import {FormsModule} from "@angular/forms";
import {Table, TableModule} from "primeng/table";
import {IconFieldModule} from "primeng/iconfield";
import {InputIconModule} from "primeng/inputicon";
import {InputTextModule} from "primeng/inputtext";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {ConfirmationService, MessageService} from "primeng/api";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {ToastModule} from "primeng/toast";
import {SelectChessEventComponent} from "../select-chess-event/select-chess-event.component";
import {SelectChessPersonComponent} from "../select-chess-person/select-chess-person.component";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {ChessGameFormComponent} from "../chess-game-form/chess-game-form.component";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-chess-update-game',
  imports: [
    FieldsetModule,
    MultiSelectModule,
    FormsModule,
    TableModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    Button,
    CardModule,
    ConfirmDialogModule,
    ToastModule,
    SelectChessEventComponent,
    SelectChessPersonComponent,
    ChessGameFormComponent,
    DatePipe
  ],
  templateUrl: './chess-update-game.component.html',
  styleUrl: './chess-update-game.component.css'
})
export class ChessUpdateGameComponent {
  private readonly chessService = inject(ChessService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly messageService = inject(MessageService);

  events = rxResource({
    stream: () => this.chessService.events(),
    defaultValue: []
  })
  selectedEvent = signal<ChessEvent | undefined>(undefined)

  persons = rxResource({
    params: () => ({eventId: this.selectedEvent()?.id}),
    stream: (params) => {
      const eventId = params.params.eventId
      if(eventId == undefined)
        return of([])
      return this.chessService.eventParticipants(eventId)
    },
    defaultValue: []
  })
  selectedPersons = signal<Person[]>([]);

  accounts = computed(() => this.selectedPersons().flatMap(value => value.accounts))
  selectedAccounts = signal<Account[]>([]);

  games = rxResource({
    params: () => ({ accountIds: this.selectedAccounts().map(a => a.id) }),
    stream: (params) => {
      const ids = params.params.accountIds
      if (ids.length !== 2) return of([])
      if (ids.some(id => id === undefined)) return of([])
      const castedIds = ids as string[]
      return this.chessService.getGamesBetweenAccounts(castedIds)
    },
    defaultValue: [] as ChessGame[]
  })
  selectedGame = signal<ChessGame | undefined>(undefined)
  accountTableSearch: string = "";

  constructor() {
    effect(() => {
      this.selectedEvent();
      untracked(() => {
        this.selectedPersons.set([]);
        this.selectedAccounts.set([]);
        this.selectedGame.set(undefined);
      });
    });
    effect(() => {
      this.selectedPersons();
      untracked(() => {
        this.selectedAccounts.set([]);
        this.selectedGame.set(undefined);
      });
    });
    effect(() => {
      this.selectedAccounts();
      untracked(() => {
        this.selectedGame.set(undefined);
      });
    });
  }

  clear(table: Table) {
    table.clear();
    this.selectedAccounts.set([])
    this.onSelectedAccountsChange()
  }

  onPersonsSelect(person: Person[] | Person | undefined) {
    if(person === undefined) {
      this.selectedPersons.set([])
      return
    }
    const persons = Array.isArray(person) ? person : [person]
    this.selectedPersons.set(persons)
  }

  onSelectedAccountsChange() {
    this.selectedGame.set(undefined)
  }

  deleteGame(event: Event, game: ChessGame) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure that you want to delete this game?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptIcon: "none",
      rejectIcon: "none",
      rejectButtonStyleClass: "p-button-text",
      accept: () => {
        if (game.id) {
          this.chessService.deleteGame(game.id).subscribe(() => {
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Game deleted'});
            this.games.reload();
            if (this.selectedGame()?.id === game.id) {
              this.selectedGame.set(undefined);
            }
          });
        }
      }
    });
  }
}


