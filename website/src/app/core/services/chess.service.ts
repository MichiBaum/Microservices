import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Person, SearchPerson} from "../models/chess.models";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({providedIn: 'root'})
export class ChessService {

  constructor(private http: HttpClient) {
  }


  search(searchPerson: SearchPerson): Observable<Person[]> {
    return this.http.post<Person[]>(environment.chessService + '/persons/search', searchPerson)
  }

}
