import {Component, inject, OnInit, signal} from '@angular/core';
import {Card} from "primeng/card";
import {Button} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {TranslatePipe} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {EventIconPipe} from "../../core/pipes/gender-icon.pipe";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgIf} from "@angular/common";
import {Person, SearchPerson} from "../../core/models/chess/chess.models";
import {ChessService} from "../../core/api-services/chess.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-chess-person-search',
    imports: [
        Card,
        Button,
        FloatLabel,
        FormsModule,
        InputText,
        ReactiveFormsModule,
        TranslatePipe,
        TableModule,
        EventIconPipe,
        FaIconComponent,
        NgIf,
        RouterLink
    ],
  templateUrl: './chess-person-search.component.html',
  styleUrl: './chess-person-search.component.css'
})
export class ChessPersonSearchComponent implements OnInit{
    chessService = inject(ChessService);
    route = inject(ActivatedRoute);
    router = inject(Router);

    persons = signal<Person[]>([])

    searchForm: FormGroup = new FormGroup({
        firstname: new FormControl<string>({
            value: '',
            disabled: false
        }, [
        ]),
        lastname: new FormControl<string>({
            value: '',
            disabled: false
        }, [
        ])
    })

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            if (params['firstname']) {
                this.searchForm.controls['firstname'].setValue(params['firstname']);
            }
            if (params['lastname']) {
                this.searchForm.controls['lastname'].setValue(params['lastname']);
            }
            if (params['firstname'] || params['lastname']) {
                this.search();
            }
        });
    }


    search() {
        if(!this.searchForm.valid)
            return
        const firstname: string = this.searchForm.controls['firstname'].value ?? ""
        const lastname: string = this.searchForm.controls['lastname'].value ?? ""
        const searchPerson: SearchPerson = {
            firstname: firstname,
            lastname: lastname,
        }

        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                ...(firstname && {firstname}),
                ...(lastname && {lastname})
            },
            queryParamsHandling: 'replace'
        });

        this.chessService.searchPersons(searchPerson).subscribe(persons => {
            this.persons.set(persons)
        })
    }

}
