import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Gender, Person} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {PrimeTemplate} from "primeng/api";
import {Table, TableModule} from "primeng/table";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-select-chess-person',
  standalone: true,
  imports: [
    InputTextModule,
    PaginatorModule,
    PrimeTemplate,
    TableModule,
    FaIconComponent
  ],
  templateUrl: './select-chess-person.component.html',
  styleUrl: './select-chess-person.component.scss'
})
export class SelectChessPersonComponent {
  @Input()
  persons: Person[] = [];

  @Output()
  selectedPersonsEmitter: EventEmitter<Person[]> = new EventEmitter()

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
