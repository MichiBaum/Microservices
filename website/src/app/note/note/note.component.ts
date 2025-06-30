import {Component, input} from '@angular/core';
import {NoteResponse} from "../../core/models/alexandria/note.model";
import {Card} from "primeng/card";

@Component({
  selector: 'app-note',
    imports: [
        Card
    ],
  templateUrl: './note.component.html',
  styleUrl: './note.component.css'
})
export class NoteComponent {
    note = input.required<NoteResponse>();
}
