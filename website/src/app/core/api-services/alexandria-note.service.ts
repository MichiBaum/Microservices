import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {EnvironmentConfig} from "../config/environment.config";
import {Observable} from "rxjs";
import {NoteResponse, NoteRequest} from "../models/alexandria/note.model";

@Injectable({providedIn: 'root'})
export class AlexandriaNoteService {
    private http = inject(HttpClient);
    private readonly environment = inject(EnvironmentConfig);

    notes(): Observable<NoteResponse[]> {
        return this.http.get<NoteResponse[]>(this.environment.alexandriaService() + '/notes');
    }

    update(note: NoteRequest) {
        return this.http.put<NoteResponse[]>(this.environment.alexandriaService() + '/notes', note);
    }

    create(note: NoteRequest) {
        return this.http.post<NoteResponse[]>(this.environment.alexandriaService() + '/notes', note);
    }
}
