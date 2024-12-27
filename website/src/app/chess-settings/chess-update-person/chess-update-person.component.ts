import {Component, OnInit, inject, signal} from '@angular/core';
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
  private readonly chessService = inject(ChessService);

  protected readonly environment = environment;

  persons: Person[] = [];
  selectedPersonS = signal<Person | undefined>(undefined);
  genders = signal<Gender[]>([Gender.MALE, Gender.FEMALE]);

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


  ngOnInit(): void {
    this.chessService.persons().subscribe(persons => this.persons = [...persons]);
  }

  selectedPersonsChange(persons: Person[]) {
    if(persons.length == 0 || persons.length > 1){
      this.selectedPersonS.set(undefined);
      this.resetForm();
      return;
    }
    this.selectedPersonS.set(persons[0]);
    this.patchForm();
  }

  private patchForm() {
    const selectedPerson = this.selectedPersonS();
    let birthday;
    if(selectedPerson?.birthday){
      birthday = new Date(selectedPerson?.birthday)
    }

    this.formGroup?.patchValue({
      id: selectedPerson?.id ?? '',
      firstname: selectedPerson?.firstname ?? '',
      lastname: selectedPerson?.lastname ?? '',
      federation: selectedPerson?.federation ?? '',
      birthday: birthday ?? null,
      gender: selectedPerson?.gender ?? null,
    })
  }

  private resetForm() {
    this.formGroup?.reset();
  }

  save() {
    if(this.formGroup.invalid){
      return;
    }

    // TODO this is a quickfix for timezones
    let birthday: string | undefined = undefined;
    if(this.formGroup.controls['birthday'].value != undefined){
      const d = this.formGroup.controls['birthday'].value
      const offset = d.getTimezoneOffset()
      birthday = new Date(d.getTime() - (offset*60*1000)).toISOString().split('T')[0]
    }

    console.log(birthday)
    const person: WritePerson = {
      firstname: this.formGroup.controls['firstname'].value,
      lastname: this.formGroup.controls['lastname'].value,
      federation: this.formGroup.controls['federation'].value,
      birthday: birthday,
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
    this.selectedPersonS.set(undefined);
  }

  confirmDelete() {

  }
}
