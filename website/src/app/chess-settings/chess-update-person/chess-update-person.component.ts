import {Component, OnInit} from '@angular/core';
import {DropdownModule} from "primeng/dropdown";
import {DividerModule} from "primeng/divider";
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessPersonComponent} from "../select-chess-person/select-chess-person.component";
import {FileUploadModule} from "primeng/fileupload";
import {environment} from "../../../environments/environment";
import {Person} from "../../core/models/chess/chess.models";
import {ChessService} from "../../core/services/chess.service";

@Component({
  selector: 'app-chess-update-person',
  standalone: true,
  imports: [
    DropdownModule,
    DividerModule,
    FieldsetModule,
    SelectChessPersonComponent,
    FileUploadModule
  ],
  templateUrl: './chess-update-person.component.html',
  styleUrl: './chess-update-person.component.scss'
})
export class ChessUpdatePersonComponent implements OnInit{
  protected readonly environment = environment;

  persons: Person[] = [];
  selectedPerson: Person | undefined;

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
  }

  private patchForm() {

  }

  private resetForm() {

  }
}
