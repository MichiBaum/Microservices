import {Component, inject} from '@angular/core';
import {AlexandriaNoteService} from "../core/api-services/alexandria-note.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {NoteCardComponent} from "./note-card/note-card.component";

@Component({
  selector: 'app-authentication',
    imports: [
        NoteCardComponent,
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
