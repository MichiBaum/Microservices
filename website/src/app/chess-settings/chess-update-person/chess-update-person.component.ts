import {Component, OnInit} from '@angular/core';
import {DropdownModule} from "primeng/dropdown";
import {DividerModule} from "primeng/divider";
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessPersonComponent} from "../select-chess-person/select-chess-person.component";
import {FileUploadModule} from "primeng/fileupload";
import {environment} from "../../../environments/environment";
import {Gender, Person, WritePerson} from "../../core/models/chess/chess.models";
import {ChessService} from "../../core/services/chess.service";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Button} from "primeng/button";
import {DatePicker} from "primeng/datepicker";
import {Select} from "primeng/select";
import {FloatLabel} from "primeng/floatlabel";

@Component({
  selector: 'app-chess-update-person',
  standalone: true,
  imports: [
    DropdownModule,
    DividerModule,
    FieldsetModule,
    SelectChessPersonComponent,
    FileUploadModule,
    InputGroupAddonModule,
    InputGroupModule,
    InputTextModule,
    PaginatorModule,
    ReactiveFormsModule,
    Button,
    DatePicker,
    Select,
    FloatLabel
  ],
  templateUrl: './chess-update-person.component.html',
  styleUrl: './chess-update-person.component.scss'
})
export class ChessUpdatePersonComponent implements OnInit{
  protected readonly environment = environment;

  persons: Person[] = [];
  selectedPerson: Person | undefined;
  genders: any[] = [Gender.MALE, Gender.FEMALE];

  formGroup: FormGroup = new FormGroup({
    id: new FormControl<string | null>({
      value: '',
      disabled: true
    }),
    firstname: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    lastname: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    fideId: new FormControl<string>({
      value: '',
      disabled: false
    }, [
    ]),
    federation: new FormControl<string>({
      value: '',
      disabled: false
    }, [
    ]),
    birthday: new FormControl<Date | null>({
      value: null,
      disabled: false
    }, [
    ]),
    gender: new FormControl<Gender | null>({
      value: null,
      disabled: false
    }, [
      Validators.required,
    ]),
  })

  constructor(
    private readonly chessService: ChessService
  ) { }

  ngOnInit(): void {
    this.chessService.persons().subscribe(persons => this.persons = [...persons]);
  }

  selectedPersonsChange(persons: Person[]) {
    if(persons.length == 0 || persons.length > 1){
      this.selectedPerson = undefined;
      this.resetForm();
      return;
    }
    this.selectedPerson = persons[0];
    this.patchForm();
  }

  private patchForm() {
    let birthday;
    if(this.selectedPerson?.birthday){
      birthday = new Date(this.selectedPerson?.birthday)
    }

    this.formGroup?.patchValue({
      id: this.selectedPerson?.id ?? '',
      firstname: this.selectedPerson?.firstname ?? '',
      lastname: this.selectedPerson?.lastname ?? '',
      fideId: this.selectedPerson?.fideId ?? '',
      federation: this.selectedPerson?.federation ?? '',
      birthday: birthday ?? null,
      gender: this.selectedPerson?.gender ?? null,
    })
  }

  private resetForm() {
    this.formGroup?.reset();
  }

  save() {
    if(this.formGroup.invalid){
      return;
    }

    const person: WritePerson = {
      firstname: this.formGroup.controls['firstname'].value,
      lastname: this.formGroup.controls['lastname'].value,
      fideId: this.formGroup.controls['fideId'].value,
      federation: this.formGroup.controls['federation'].value,
      birthday: this.formGroup.controls['birthday'].value?.toISOString().split('T')[0] ?? null,
      gender: this.formGroup.controls['gender'].value,
    }

    const id = this.formGroup.controls['id'].value ?? ""
    this.chessService.savePerson(id, person).subscribe(newPerson => {
      this.clear()
      let isNewPerson = !this.persons.some(old => old.id === newPerson.id);
      if (isNewPerson){
        this.persons = [...this.persons, newPerson]
      } else {
        const newPersons = this.persons.map(old => old.id === newPerson.id ? newPerson : old);
        this.persons = [...newPersons]
      }
    })

  }

  clear() {
    this.formGroup.reset();
    this.selectedPerson = undefined;
  }

  confirmDelete() {

  }
}
