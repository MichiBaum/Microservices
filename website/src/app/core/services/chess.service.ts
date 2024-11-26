import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Person, SearchPerson} from "../models/chess/chess.models";
import {catchError, Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";
import {ChessEvent} from "../models/chess/chess-event.models";
import {EventParticipant} from "../models/chess/event-participant.model";

@Injectable({providedIn: 'root'})
export class ChessService {

  constructor(private http: HttpClient, private httpErrorConfig: HttpErrorHandler, private userInfoService: UserInfoService) {
  }

  search(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(environment.chessService + '/persons/search', searchPerson)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  events(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(environment.chessService + '/events')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  event(id: string): Observable<ChessEvent> {
    return this.http.get<ChessEvent>(environment.chessService + '/events/' + id)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventParticipants(id: string): Observable<EventParticipant[]> {
    return this.http.get<EventParticipant[]>(environment.chessService + '/events/' + id + "/participants")
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }
}
