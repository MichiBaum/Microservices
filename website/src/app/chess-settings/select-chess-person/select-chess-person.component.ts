import {Component, input, output, signal} from '@angular/core';
import {Person} from "../../core/models/chess/chess.models";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {TableModule} from "primeng/table";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {EventIconPipe} from "../../core/pipes/gender-icon.pipe";

@Component({
  selector: 'app-select-chess-person',
  imports: [
    InputTextModule,
    PaginatorModule,
    TableModule,
    FaIconComponent,
    EventIconPipe
  ],
  templateUrl: './select-chess-person.component.html',
  styleUrl: './select-chess-person.component.scss'
})
export class SelectChessPersonComponent {
  readonly persons = input<Person[]>([]);
  readonly selectedPersonsEmitter = output<Person[]>();

  selectedPersons = signal<Person[]>([]);


  onSelectionChange() {
    // TODO solution if input single, multiple. Doesnt work yet because table is always a step behind
    //  if(this.selectedPersons().length > 1) {
    //    const lastAdded = this.selectedPersons().pop()
    //    if(lastAdded){
    //      this.selectedPersons.set([lastAdded])
    //    }
    //  }
    this.selectedPersonsEmitter.emit(this.selectedPersons())
  }

}
