import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Person, SearchPerson} from "../models/chess.models";
import {catchError, Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";

@Injectable({providedIn: 'root'})
export class ChessService {

  constructor(private http: HttpClient, private httpErrorConfig: HttpErrorHandler, private userInfoService: UserInfoService) {
  }


  search(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(environment.chessService + '/persons/search', searchPerson)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

}
