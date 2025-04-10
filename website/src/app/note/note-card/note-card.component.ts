import {Component, input} from '@angular/core';
import {NoteResponse} from "../../core/models/alexandria/note.model";
import {Card} from "primeng/card";

@Component({
  selector: 'app-note',
    imports: [
        Card
    ],
  templateUrl: './note-card.component.html',
  styleUrl: './note-card.component.css'
})
export class NoteCardComponent {
    note = input.required<NoteResponse>();
}
