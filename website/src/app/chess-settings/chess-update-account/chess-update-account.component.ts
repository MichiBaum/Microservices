import {Component, effect, inject, signal, ViewChild} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessAccountComponent} from "../select-chess-account/select-chess-account.component";
import {SearchChessAccountComponent} from "../search-chess-account/search-chess-account.component";
import {ChessService} from "../../core/api-services/chess.service";
import {InputTextModule} from "primeng/inputtext";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Button} from "primeng/button";
import {Account, ChessPlatform, WriteAccount} from "../../core/models/chess/chess.models";
import {FloatLabel} from "primeng/floatlabel";
import {rxResource} from "@angular/core/rxjs-interop";
import {Select} from "primeng/select";
import {DatePicker} from "primeng/datepicker";
import {UserConfirmationService} from "../../core/services/user-confirmation.service";

@Component({
  selector: 'app-chess-update-account',
  imports: [
    FieldsetModule,
    InputTextModule,
    Button,
    FloatLabel,
    ReactiveFormsModule,
    Select,
    DatePicker,
    SelectChessAccountComponent,
    SearchChessAccountComponent
  ],
  templateUrl: './chess-update-account.component.html',
  styleUrl: './chess-update-account.component.css'
})
export class ChessUpdateAccountComponent{
  private readonly chessService = inject(ChessService);
  private readonly confirmationService = inject(UserConfirmationService);

  @ViewChild('accountSearch') accountSearch!: SearchChessAccountComponent;

  foundAccounts = signal<Account[]>([])

  allPersons = rxResource({
    stream: () => this.chessService.persons(),
    defaultValue: []
  });

  platforms = Object.values(ChessPlatform)
  selectedAccount = signal<Account | undefined>(undefined)

  formGroup: FormGroup = new FormGroup({
    id: new FormControl<string | null>({
      value: '',
      disabled: true
    }),
    platformId: new FormControl<string>('', [Validators.required]),
    name: new FormControl<string>('', [Validators.required]),
    username: new FormControl<string>('', [Validators.required]),
    platform: new FormControl<ChessPlatform | null>(null, [Validators.required]),
    createdAt: new FormControl<Date | null>(null),
    personId: new FormControl<string | null>(null)
  })

  constructor() {
    effect(() => {
      const selectedAccount = this.selectedAccount();
      this.patchForm(selectedAccount);
    });
  }

  patchForm(selectedAccount: Account | undefined){
    if(selectedAccount == undefined){
      this.clearForm()
      return;
    }

    let createdAt;
    if(selectedAccount.createdAt){
      createdAt = new Date(selectedAccount.createdAt)
    }

    this.formGroup?.patchValue({
      id: selectedAccount.id ?? '',
      platformId: selectedAccount.platformId ?? '',
      name: selectedAccount.name ?? '',
      username: selectedAccount.username ?? '',
      platform: selectedAccount.platform ?? null,
      createdAt: createdAt ?? null,
      personId: selectedAccount.person?.id ?? null,
    });
  }

  save() {
    if(this.formGroup.invalid){
      return;
    }

    const createdAt = this.getDate(this.formGroup.controls['createdAt'].value)

    const account: WriteAccount = {
      platformId: this.formGroup.controls['platformId'].value,
      name: this.formGroup.controls['name'].value,
      username: this.formGroup.controls['username'].value,
      platform: this.formGroup.controls['platform'].value,
      createdAt: createdAt,
      personId: this.formGroup.controls['personId'].value
    };

    const id = this.formGroup.getRawValue().id ?? ""
    this.chessService.saveAccount(id, account).subscribe(newAccount => {
      this.clear()
      this.accountSearch.reload()
    })
  }

  getDate(date: Date): string | undefined {
    let dateString: string | undefined = undefined;
    if(date != undefined){
      const offset = date.getTimezoneOffset()
      dateString = new Date(date.getTime() - (offset*60*1000)).toISOString().split('T')[0]
    }
    return dateString;
  }

  clear() {
    this.clearForm()
    this.selectedAccount.set(undefined);
  }

  clearForm() {
    this.formGroup.reset();
  }

  delete() {
    const id = this.selectedAccount()?.id;
    if (!id)
      return;

    this.confirmationService.deleteConfirm({
      message: 'Are you sure you want to delete this account?',
      header: 'Delete Confirmation',
      accept: () => {
          this.chessService.deleteAccount(id).subscribe(() => {
            this.clear();
            this.accountSearch.reload();
          });
      }
    });
  }


  importGames() {
    const id = this.selectedAccount()?.id;
    if (!id)
      return;

    this.chessService.importGames(id).subscribe(nothing => {

    })
  }

  showImportGameButton() {
    const account = this.selectedAccount()
    if (!account)
      return false

    let isOverTheBoard = account.platform === ChessPlatform.FIDE;
    return !isOverTheBoard
  }

}


