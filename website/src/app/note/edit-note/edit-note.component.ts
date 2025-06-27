import {Component, inject} from '@angular/core';
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {Editor} from "primeng/editor";
import {Button} from "primeng/button";
import {AlexandriaNoteService} from "../../core/api-services/alexandria-note.service";

@Component({
  selector: 'app-edit-note',
    imports: [
        FloatLabel,
        InputText,
        ReactiveFormsModule,
        TranslatePipe,
        Editor,
        Button
    ],
  templateUrl: './edit-note.component.html',
  styleUrl: './edit-note.component.css'
})
export class EditNoteComponent {

    noteService = inject(AlexandriaNoteService)

    noteForm: FormGroup = new FormGroup({
        id: new FormControl<string | null>({
            value: '',
            disabled: true
        }),
        title: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ]),
        text: new FormControl<string>({
            value: '',
            disabled: false
        }, [
            Validators.required,
        ]),
    });

    save() {
        this.noteService.saveNote(this.noteForm.value)
    }
}
