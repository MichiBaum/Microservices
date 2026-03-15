import {Component, effect, inject, signal} from '@angular/core';
import {FieldsetModule} from "primeng/fieldset";
import {ChessService} from "../../core/api-services/chess.service";
import {InputTextModule} from "primeng/inputtext";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Button, ButtonDirective} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {Account, ChessPlatform, SearchLocation, WriteAccount} from "../../core/models/chess/chess.models";
import {TableModule} from "primeng/table";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {CheckboxModule} from "primeng/checkbox";
import {FloatLabel} from "primeng/floatlabel";
import {rxResource} from "@angular/core/rxjs-interop";
import {of} from "rxjs";
import {SelectButton} from "primeng/selectbutton";
import {Select} from "primeng/select";
import {DatePicker} from "primeng/datepicker";
import {UserConfirmationService} from "../../core/services/user-confirmation.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-chess-update-account',
  imports: [
    FieldsetModule,
    InputTextModule,
    FormsModule,
    Button,
    InputGroupModule,
    ButtonDirective,
    TableModule,
    InputGroupAddonModule,
    CheckboxModule,
    FloatLabel,
    ReactiveFormsModule,
    SelectButton,
    Select,
    DatePicker,
    FaIconComponent
  ],
  templateUrl: './chess-update-account.component.html',
  styleUrl: './chess-update-account.component.css'
})
export class ChessUpdateAccountComponent{
  private readonly chessService = inject(ChessService);
  private readonly routerService = inject(RouterNavigationService);
  private readonly confirmationService = inject(UserConfirmationService);

  searchTerm = signal('');
  searchLocation = signal<SearchLocation>(SearchLocation.LOCAL);

  accounts = rxResource({
    stream: () => {
      const searchTerm = this.searchTerm()
      const localSearch = this.searchLocation()
      if (searchTerm === undefined || searchTerm.length < 1)
        return of([]);
      return this.chessService.accountsSearch(searchTerm, localSearch)
    },
    defaultValue: []
  })

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
      this.accounts.reload()
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

  delete(id?: string) {
    const accountId = id ?? this.selectedAccount()?.id;
    if (!accountId)
      return;

    this.confirmationService.deleteConfirm({
      message: 'Are you sure you want to delete this account?',
      header: 'Delete Confirmation',
      accept: () => {
          this.chessService.deleteAccount(accountId).subscribe(() => {
            this.clear();
            this.accounts.reload();
          });
      }
    });
  }

  open(url: string) {
    this.routerService.open(url)
  }

  importGames(id: string) {
    this.chessService.importGames(id).subscribe(nothing => {

    })
  }

  showImportGameButton(account: Account) {
    let isOverTheBoard = account.platform === ChessPlatform.FIDE;
    return !isOverTheBoard
  }

  protected searchLocationOptions(): SearchLocation[] {
    return Object.values(SearchLocation);
  }

  selectAccount(account: Account) {
    this.selectedAccount.set(account);
  }

  protected readonly faCheck = faCheck;
  protected readonly faXmark = faXmark;
}


