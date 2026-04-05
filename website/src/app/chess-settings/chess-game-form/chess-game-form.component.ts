import {Component, effect, inject, input, model, output, signal} from '@angular/core';
import {ChessService} from "../../core/api-services/chess.service";
import {MessageService} from "primeng/api";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  Account,
  ChessEvent,
  ChessGame,
  ChessPlatform,
  GameResult,
  GameVariant,
  PieceColor,
  SourceType,
  TerminationType,
  TimeControlCategory,
  WriteChessGame
} from "../../core/models/chess/chess.models";
import {Fieldset} from "primeng/fieldset";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {Select} from "primeng/select";
import {DatePicker} from "primeng/datepicker";
import {Button} from "primeng/button";
import {Textarea} from "primeng/textarea";
import {InputNumber} from "primeng/inputnumber";
import {Card} from "primeng/card";

@Component({
  selector: 'app-chess-game-form',
  imports: [
    ReactiveFormsModule,
    Fieldset,
    FloatLabel,
    InputText,
    Select,
    DatePicker,
    Button,
    Textarea,
    InputNumber,
    Card,
    FormsModule
  ],
  templateUrl: './chess-game-form.component.html',
  styleUrl: './chess-game-form.component.css'
})
export class ChessGameFormComponent {
  private readonly chessService = inject(ChessService);
  private readonly messageService = inject(MessageService);

  sourceTypes = Object.values(SourceType);
  platforms = Object.values(ChessPlatform);
  gameResults = Object.values(GameResult);
  terminationTypes = Object.values(TerminationType);
  timeControlCategories = Object.values(TimeControlCategory);
  gameVariants = Object.values(GameVariant);

  selectedEvent = input.required<ChessEvent | undefined>();
  selectedAccounts = input.required<Account[]>();

  selectedGame = model<ChessGame | undefined>(undefined);
  saved = output<void>();

  formGroup = new FormGroup({
    id: new FormControl<string | null>({value: '', disabled: true}),
    sourceType: new FormControl<SourceType>(SourceType.OTB, [Validators.required]),
    platform: new FormControl<ChessPlatform>(ChessPlatform.FIDE, [Validators.required]),
    externalGameId: new FormControl<string | null>(null),
    pgn: new FormControl<string>(''),
    openingName: new FormControl<string | null>(null),
    gameResult: new FormControl<GameResult>(GameResult.NOT_STARTED, [Validators.required]),
    termination: new FormControl<TerminationType | null>(null),
    playedAt: new FormControl<Date>(new Date(), [Validators.required]),
    timeControl: new FormControl<string>('', [Validators.required]),
    timeControlCategory: new FormControl<TimeControlCategory>(TimeControlCategory.CLASSICAL, [Validators.required]),
    variant: new FormControl<GameVariant>(GameVariant.STANDARD, [Validators.required]),
    eventId: new FormControl<string | null>(null, [Validators.required]),

    whitePlayer: new FormGroup({
      accountId: new FormControl<string | null>(null, [Validators.required]),
      username: new FormControl<string>('', [Validators.required]),
      rating: new FormControl<number | null>(null),
    }),
    blackPlayer: new FormGroup({
      accountId: new FormControl<string | null>(null, [Validators.required]),
      username: new FormControl<string>('', [Validators.required]),
      rating: new FormControl<number | null>(null),
    })
  });

  constructor() {
    effect(() => {
      const selectedGame = this.selectedGame();
      this.patchForm(selectedGame);
    });
    effect(() => {
      const event = this.selectedEvent();
      if (event) {
        this.formGroup.patchValue({eventId: event.id});
      }
    });
    effect(() => {
      const accounts = this.selectedAccounts();
      if (accounts.length === 2) {
        this.formGroup.patchValue({
          whitePlayer: {
            accountId: accounts[0].id,
            username: accounts[0].username
          },
          blackPlayer: {
            accountId: accounts[1].id,
            username: accounts[1].username
          }
        });
        if (accounts[0].platform === accounts[1].platform) {
          this.formGroup.patchValue({platform: accounts[0].platform});
        }
      }
    });
  }

  patchForm(game: ChessGame | undefined) {
    if (game == undefined) {
      this.resetForm();
      return;
    }

    this.formGroup.patchValue({
      id: game.id,
      sourceType: game.sourceType,
      platform: game.platform,
      externalGameId: game.externalGameId,
      pgn: game.pgn,
      openingName: game.openingName,
      gameResult: game.gameResult,
      termination: game.termination,
      playedAt: new Date(game.playedAt),
      timeControl: game.timeControl,
      timeControlCategory: game.timeControlCategory,
      variant: game.variant,
      whitePlayer: {
        username: game.whitePlayer.username,
        rating: game.whitePlayer.rating,
        accountId: game.whitePlayer.accountId
      },
      blackPlayer: {
        username: game.blackPlayer.username,
        rating: game.blackPlayer.rating,
        accountId: game.blackPlayer.accountId
      }
    });
  }

  save() {
    if (this.formGroup.invalid) {
      return;
    }

    const formValue = this.formGroup.getRawValue();

    if(formValue.whitePlayer.accountId == undefined || formValue.blackPlayer.accountId == undefined || formValue.whitePlayer.accountId ==  formValue.blackPlayer.accountId){
      return;
    }

    const game: WriteChessGame = {
      sourceType: formValue.sourceType!,
      platform: formValue.platform,
      externalGameId: formValue.externalGameId,
      pgn: formValue.pgn!,
      openingName: formValue.openingName,
      gameResult: formValue.gameResult!,
      termination: formValue.termination,
      playedAt: this.getDate(formValue.playedAt!)!,
      timeControl: formValue.timeControl,
      timeControlCategory: formValue.timeControlCategory,
      variant: formValue.variant!,
      whitePlayer: {
        accountId: formValue.whitePlayer.accountId,
        username: formValue.whitePlayer.username,
        rating: formValue.whitePlayer.rating,
      },
      blackPlayer: {
        accountId: formValue.blackPlayer.accountId,
        username: formValue.blackPlayer.username,
        rating: formValue.blackPlayer.rating,
      },
      eventId: formValue.eventId
    };

    const id = this.formGroup.controls['id'].value ?? "";
    this.chessService.saveGame(id, game).subscribe(() => {
      this.messageService.add({severity: 'success', summary: 'Success', detail: 'Game saved successfully'});
      this.saved.emit();
      this.clear();
    });
  }

  clear() {
    this.selectedGame.set(undefined);
    this.resetForm();
  }

  resetForm() {
    this.formGroup.reset({
      sourceType: SourceType.OTB,
      gameResult: GameResult.NOT_STARTED,
      playedAt: new Date(),
      variant: GameVariant.STANDARD
    });

    const event = this.selectedEvent();
    if (event) {
      this.formGroup.patchValue({eventId: event.id});
    }

    const accounts = this.selectedAccounts();
    if (accounts.length === 2) {
      this.formGroup.patchValue({
        whitePlayer: {
          accountId: accounts[0].id,
          username: accounts[0].username
        },
        blackPlayer: {
          accountId: accounts[1].id,
          username: accounts[1].username
        }
      });
      if (accounts[0].platform === accounts[1].platform) {
        this.formGroup.patchValue({platform: accounts[0].platform});
      }
    }
  }

  getDate(date: Date): string | undefined {
    if (date == undefined) return undefined;
    const offset = date.getTimezoneOffset();
    return new Date(date.getTime() - (offset * 60 * 1000)).toISOString();
  }

  protected onAccountChange(color: PieceColor, accountId: string) {
    const selectedAccount = this.selectedAccounts().find(account => account.id === accountId);
    if (!selectedAccount) {
      return
    }
    if (color === PieceColor.WHITE) {
      this.formGroup.controls['whitePlayer'].controls['username'].patchValue(selectedAccount.username);
    } else if (color === PieceColor.BLACK) {
      this.formGroup.controls['blackPlayer'].controls['username'].patchValue(selectedAccount.username);
    }
  }

  protected readonly PieceColor = PieceColor;
}
