import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {EnvironmentConfig} from "../config/environment.config";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {catchError, Observable} from "rxjs";
import {Note} from "../models/alexandria/note.model";
import {UserInfoService} from "../services/user-info.service";

@Injectable({providedIn: 'root'})
export class AlexandriaNoteService {
    private http = inject(HttpClient);
    private readonly environment = inject(EnvironmentConfig);
    private httpErrorConfig = inject(HttpErrorHandler);
    private userInfoService = inject(UserInfoService);

    notes(): Observable<Note[]> {
        return this.http.get<Note[]>(this.environment.alexandriaService() + '/notes')
            .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
    }

}
