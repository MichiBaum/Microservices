import {Component, inject} from '@angular/core';
import {AlexandriaNoteService} from "../core/api-services/alexandria-note.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {NgForOf} from "@angular/common";
import {NoteComponent} from "./note/note.component";
import {EditNoteComponent} from "./edit-note/edit-note.component";

@Component({
  selector: 'app-authentication',
    imports: [
        NgForOf,
        NoteComponent,
        EditNoteComponent
    ],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent {
    notesService = inject(AlexandriaNoteService)

    notes = rxResource({
        stream: () => this.notesService.notes()
    })

    constructor() {
    }
}
