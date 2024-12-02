import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Account, ChessEvent, ChessEventCategory, ChessGame, Person, SearchPerson} from "../models/chess/chess.models";
import {catchError, Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";

@Injectable({providedIn: 'root'})
export class ChessService {

  constructor(private http: HttpClient, private httpErrorConfig: HttpErrorHandler, private userInfoService: UserInfoService) {
  }

  searchPersons(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(environment.chessService + '/persons/search', searchPerson)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  persons(): Observable<Person[]> {
    return this.http.get<Person[]>(environment.chessService + '/persons')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  events(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(environment.chessService + '/events')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventsRecentUpcoming(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(environment.chessService + '/events/recent-upcoming')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  event(id: string): Observable<ChessEvent> {
    return this.http.get<ChessEvent>(environment.chessService + '/events/' + id)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventParticipants(id: string): Observable<Person[]> {
    return this.http.get<Person[]>(environment.chessService + '/events/' + id + "/participants")
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventCategories(): Observable<ChessEventCategory[]> {
    return this.http.get<ChessEventCategory[]>(environment.chessService + '/events/categories')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventGames(id: string): Observable<ChessGame[]> {
    return this.http.get<ChessGame[]>(environment.chessService + '/events/' + id + "/games")
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  accountsSearch(name: string, local: boolean): Observable<Account[]> {
    return this.http.get<Account[]>(environment.chessService + '/accounts/search/' + name + '?local=' + local)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  accounts(): Observable<Account[]> {
    return this.http.get<Account[]>(environment.chessService + '/accounts')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  importGames(id: string): Observable<void> {
    return this.http.get<void>(environment.chessService + '/accounts/' + id + '/load-games')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }
}
