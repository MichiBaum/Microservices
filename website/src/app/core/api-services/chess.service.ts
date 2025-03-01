import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {
    Account,
    ChessEngine,
    ChessEvent,
    ChessEventCategory,
    ChessEventCategoryWithEvents,
    ChessGame,
    ChessOpening, ChessOpeningMove,
    Person,
    PopularChessOpening,
    SearchChessEvent,
    SearchPerson,
    WriteChessEngine,
    WriteChessEvent,
    WriteChessEventCategory, WriteOpeningMove,
    WritePerson
} from "../models/chess/chess.models";
import {catchError, Observable} from "rxjs";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "../services/user-info.service";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class ChessService {
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);

  searchPersons(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(this.environment.chessService() + '/persons/search', searchPerson)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  persons(): Observable<Person[]> {
    return this.http.get<Person[]>(this.environment.chessService() + '/persons')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  events(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  searchEvents(search: SearchChessEvent): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events/search', { params: {...search} })
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventsRecentUpcoming(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events/recent-upcoming')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  event(id: string): Observable<ChessEvent> {
    return this.http.get<ChessEvent>(this.environment.chessService() + '/events/' + id)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventParticipants(id: string): Observable<Person[]> {
    return this.http.get<Person[]>(this.environment.chessService() + '/events/' + id + "/participants")
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventCategories(): Observable<ChessEventCategory[]> {
    return this.http.get<ChessEventCategory[]>(this.environment.chessService() + '/event-categories')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventCategoriesWithEvents(): Observable<ChessEventCategoryWithEvents[]> {
    return this.http.get<ChessEventCategoryWithEvents[]>(this.environment.chessService() + '/event-categories/with-events')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  eventGames(id: string): Observable<ChessGame[]> {
    return this.http.get<ChessGame[]>(this.environment.chessService() + '/events/' + id + "/games")
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  accountsSearch(name: string, local: boolean): Observable<Account[]> {
    return this.http.get<Account[]>(this.environment.chessService() + '/accounts/search/' + name + '?local=' + local)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  accounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.environment.chessService() + '/accounts')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  importGames(id: string): Observable<void> {
    return this.http.get<void>(this.environment.chessService() + '/accounts/' + id + '/load-games')
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
    return this.http.put<ChessEvent>(this.environment.chessService() + endPoint, event)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  savePerson(id: string, person: WritePerson): Observable<Person>{
    let endPoint = "/persons"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<Person>(this.environment.chessService() + endPoint, person)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  saveCategory(id: string, category: WriteChessEventCategory): Observable<ChessEventCategory> {
    let endPoint = "/event-categories"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<ChessEventCategory>(this.environment.chessService() + endPoint, category)
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

  engines(): Observable<ChessEngine[]> {
    return this.http.get<ChessEngine[]>(this.environment.chessService() + '/engines')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  createEngine(engine: WriteChessEngine): Observable<WriteChessEngine> {
    return this.http.put<WriteChessEngine>(this.environment.chessService() + '/engines', engine)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  updateEngine(id: string, engine: WriteChessEngine): Observable<WriteChessEngine> {
    return this.http.post<WriteChessEngine>(this.environment.chessService() + `/engines/${id}`, engine)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  openings(): Observable<ChessOpening[]> {
    return this.http.get<ChessOpening[]>(this.environment.chessService() + '/openings')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

    startingOpenings(): Observable<ChessOpening[]> {
        return this.http.get<ChessOpening[]>(this.environment.chessService() + '/openings/starting')
            .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
    }

    popularOpenings(): Observable<PopularChessOpening[]> {
        return this.http.get<PopularChessOpening[]>(this.environment.chessService() + '/openings/popular')
            .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
    }

  openingMoves(id: string): Observable<ChessOpeningMove> {
      return this.http.get<ChessOpeningMove>(this.environment.chessService() + `/openings/${id}/moves`)
          .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  openingChildrenMoves(id: string, maxDepth: number): Observable<ChessOpeningMove> {
      return this.http.get<ChessOpeningMove>(this.environment.chessService() + `/openings/${id}/children?maxDepth=${maxDepth}`)
          .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

    openingMove(move: WriteOpeningMove): Observable<WriteOpeningMove> {
        let endPoint = "/openings/moves"
        if(move.id !== undefined && move.id !== '')
            endPoint = endPoint + "/" + move.id
        return this.http.post<WriteOpeningMove>(this.environment.chessService() + endPoint, move)
    }

    opening(opening: ChessOpening): Observable<ChessOpening> {
        return this.http.post<ChessOpening>(this.environment.chessService() + '/openings', opening)
    }

    deleteOpening(id: string): Observable<void> {
      return this.http.delete<void>(this.environment.chessService() + '/openings/' + id)
    }

    deleteOpeningMove(id: string): Observable<void> {
      return this.http.delete<void>(this.environment.chessService() + '/openings/moves/' + id)
    }
}
