import {Component, input} from '@angular/core';
import {Note} from "../../core/models/alexandria/note.model";
import {NgIf} from "@angular/common";
import {Card} from "primeng/card";

@Component({
  selector: 'app-note',
    imports: [
        NgIf,
        Card
    ],
  templateUrl: './note.component.html',
  styleUrl: './note.component.css'
})
export class NoteComponent {
    note = input.required<Note>();
}
