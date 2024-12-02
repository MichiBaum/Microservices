import { Component } from '@angular/core';
import {DropdownModule} from "primeng/dropdown";
import {DividerModule} from "primeng/divider";
import {FieldsetModule} from "primeng/fieldset";

@Component({
  selector: 'app-chess-update-person',
  standalone: true,
  imports: [
    DropdownModule,
    DividerModule,
    FieldsetModule
  ],
  templateUrl: './chess-update-person.component.html',
  styleUrl: './chess-update-person.component.scss'
})
export class ChessUpdatePersonComponent {

  constructor(

  ) { }

}
