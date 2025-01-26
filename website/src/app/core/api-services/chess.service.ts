import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {
    Account,
    ChessEngine,
    ChessEvent,
    ChessEventCategory,
    ChessEventCategoryWithEvents,
    ChessGame,
    ChessOpening, ChessOpeningMove, OpeningEvaluation,
    Person,
    PopularChessOpening,
    SearchChessEvent,
    SearchPerson,
    WriteChessEngine,
    WriteChessEvent,
    WriteChessEventCategory, WriteOpeningEvaluation, WriteOpeningMove,
    WritePerson
} from "../models/chess/chess.models";
import {Observable, throwError} from "rxjs";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class ChessService {
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  searchPersons(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(this.environment.chessService() + '/persons/search', searchPerson)
  }

  persons(): Observable<Person[]> {
    return this.http.get<Person[]>(this.environment.chessService() + '/persons')
  }

    person(id: string): Observable<Person> {
        return this.http.get<Person>(this.environment.chessService() + '/persons/' + id)
            .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
    }

  events(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events')
  }

  searchEvents(search: SearchChessEvent): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events/search', { params: {...search} })
  }

  eventsRecentUpcoming(): Observable<ChessEvent[]> {
    return this.http.get<ChessEvent[]>(this.environment.chessService() + '/events/recent-upcoming')
  }

  event(id: string): Observable<ChessEvent> {
    return this.http.get<ChessEvent>(this.environment.chessService() + '/events/' + id)
  }

  eventParticipants(id: string): Observable<Person[]> {
    return this.http.get<Person[]>(this.environment.chessService() + '/events/' + id + "/participants")
  }

  eventCategories(): Observable<ChessEventCategory[]> {
    return this.http.get<ChessEventCategory[]>(this.environment.chessService() + '/event-categories')
  }

  eventCategoriesWithEvents(): Observable<ChessEventCategoryWithEvents[]> {
    return this.http.get<ChessEventCategoryWithEvents[]>(this.environment.chessService() + '/event-categories/with-events')
  }

  eventGames(id: string): Observable<ChessGame[]> {
    return this.http.get<ChessGame[]>(this.environment.chessService() + '/events/' + id + "/games")
  }

  accountsSearch(name: string, local: boolean): Observable<Account[]> {
    return this.http.get<Account[]>(this.environment.chessService() + '/accounts/search/' + name + '?local=' + local)
  }

  accounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.environment.chessService() + '/accounts')
  }

  importGames(id: string): Observable<void> {
    return this.http.get<void>(this.environment.chessService() + '/accounts/' + id + '/load-games')
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
  }

  savePerson(id: string, person: WritePerson): Observable<Person>{
    let endPoint = "/persons"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<Person>(this.environment.chessService() + endPoint, person)
  }

  saveCategory(id: string, category: WriteChessEventCategory): Observable<ChessEventCategory> {
    let endPoint = "/event-categories"
    if(id !== undefined && id !== '')
      endPoint = endPoint + "/" + id
    return this.http.put<ChessEventCategory>(this.environment.chessService() + endPoint, category)
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
  }

  createEngine(engine: WriteChessEngine): Observable<WriteChessEngine> {
    return this.http.put<WriteChessEngine>(this.environment.chessService() + '/engines', engine)
  }

  updateEngine(id: string, engine: WriteChessEngine): Observable<WriteChessEngine> {
    return this.http.post<WriteChessEngine>(this.environment.chessService() + `/engines/${id}`, engine)
  }

  openings(): Observable<ChessOpening[]> {
    return this.http.get<ChessOpening[]>(this.environment.chessService() + '/openings')
  }

    startingOpenings(): Observable<ChessOpening[]> {
        return this.http.get<ChessOpening[]>(this.environment.chessService() + '/openings/starting')
    }

    popularOpenings(): Observable<PopularChessOpening[]> {
        return this.http.get<PopularChessOpening[]>(this.environment.chessService() + '/openings/popular')
    }

  openingMoves(id: string): Observable<ChessOpeningMove> {
      return this.http.get<ChessOpeningMove>(this.environment.chessService() + `/openings/${id}/moves`)
  }

  openingChildrenMoves(id: string, maxDepth: number): Observable<ChessOpeningMove> {
      return this.http.get<ChessOpeningMove>(this.environment.chessService() + `/openings/${id}/children?maxDepth=${maxDepth}`)
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
      return this.http.delete<void>(this.environment.chessService() + `/openings/${id}`)
    }

    deleteOpeningMove(id: string): Observable<void> {
      return this.http.delete<void>(this.environment.chessService() + `/openings/moves/${id}`)
    }

    openingEvaluations(moveId: string): Observable<OpeningEvaluation[]> {
        return this.http.get<OpeningEvaluation[]>(this.environment.chessService() + `/openings/moves/${moveId}/evaluations`);
    }

    deleteOpeningEvaluation(id: string): Observable<boolean> {
      return this.http.delete<boolean>(this.environment.chessService() + `/openings/evaluations/${id}`);
    }

    openingEvaluation(id: string, moveId: string, evaluation: WriteOpeningEvaluation): Observable<OpeningEvaluation> {
        if(moveId === undefined || moveId == '')
            return throwError(() => new Error('Move ID is required.'));
        if(id === undefined || id == '')
            return this.http.post<OpeningEvaluation>(this.environment.chessService() + `/openings/moves/${moveId}/evaluations`, evaluation)
        else
            return this.http.put<OpeningEvaluation>(this.environment.chessService() + `/openings/moves/${moveId}/evaluations/${id}`, evaluation)
    }
}
