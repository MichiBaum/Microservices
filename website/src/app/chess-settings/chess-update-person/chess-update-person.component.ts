import {Component} from '@angular/core';
import {DropdownModule} from "primeng/dropdown";
import {DividerModule} from "primeng/divider";
import {FieldsetModule} from "primeng/fieldset";
import {SelectChessPersonComponent} from "../select-chess-person/select-chess-person.component";
import {FileUploadModule} from "primeng/fileupload";
import {environment} from "../../../environments/environment";

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
export class ChessUpdatePersonComponent {

  constructor(

  ) { }

  protected readonly environment = environment;
}
