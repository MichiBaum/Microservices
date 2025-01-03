import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {
  Account,
  ChessEvent,
  ChessEventCategory,
  ChessEventCategoryWithEvents,
  ChessGame,
  Person,
  SearchChessEvent,
  SearchPerson,
  WriteChessEvent,
  WriteChessEventCategory,
  WritePerson
} from "../models/chess/chess.models";
import {catchError, Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";

@Injectable({providedIn: 'root'})
export class ChessService {
  private http = inject(HttpClient);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);

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

  searchEvents(search: SearchChessEvent): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(environment.chessService + '/events/search', { params: {...search} })
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
    return this.http.get<ChessEventCategory[]>(environment.chessService + '/event-categories')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventCategoriesWithEvents(): Observable<ChessEventCategoryWithEvents[]> {
    return this.http.get<ChessEventCategoryWithEvents[]>(environment.chessService + '/event-categories/with-events')
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

  /**
   * Saves a chess event by sending it to the chess service endpoint. If an ID is provided, the event will be updated at the specified endpoint.
   *
   * @param id - The unique identifier of the chess event. If the ID is provided, the event will be updated; otherwise, a new event will be created.
   * @param event - The WriteChessEvent object containing the details of the chess event to be saved.
   * @return An Observable of the ChessEvent object after being successfully saved.
   */
  saveEvent(id: string, event: WriteChessEvent): Observable<ChessEvent> {
    let endPoint = "/events"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<ChessEvent>(environment.chessService + endPoint, event)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  savePerson(id: string, person: WritePerson): Observable<Person>{
    let endPoint = "/persons"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<Person>(environment.chessService + endPoint, person)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  saveCategory(id: string, category: WriteChessEventCategory): Observable<ChessEventCategory> {
    let endPoint = "/event-categories"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<ChessEventCategory>(environment.chessService + endPoint, category)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  sortEvents(events: ChessEvent[]){
    return events.sort((a, b) => {
      if (a.dateFrom == undefined && b.dateFrom == undefined) return 0;
      if (a.dateFrom == undefined) return 1;
      if (b.dateFrom == undefined) return -1;
      const dateA = new Date(a.dateFrom);
      const dateB = new Date(b.dateFrom);
      return dateA < dateB ? 1 : dateA > dateB ? -1 : 0;
    });
  }

}
