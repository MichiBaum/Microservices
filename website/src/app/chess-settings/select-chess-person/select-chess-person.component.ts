import {Component, input, output} from '@angular/core';
import {Gender, Person} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {TableModule} from "primeng/table";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-select-chess-person',
  imports: [
    InputTextModule,
    PaginatorModule,
    TableModule,
    FaIconComponent
  ],
  templateUrl: './select-chess-person.component.html',
  styleUrl: './select-chess-person.component.scss'
})
export class SelectChessPersonComponent {
  readonly persons = input<Person[]>([]);

  readonly selectedPersonsEmitter = output<Person[]>();

  selectedPersons: Person[] = [];

  constructor() {
  }

  onSelectionChange() {
      this.selectedPersonsEmitter.emit(this.selectedPersons)
  }

  getGenderIcon(person: Person) {
    if(person.gender == Gender.MALE)
      return faMars
    if (person.gender == Gender.FEMALE)
      return faVenus
    return faVenusMars;
  }

}
