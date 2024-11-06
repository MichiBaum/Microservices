import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {Token} from "../models/fitness/token.model";
import {Weight} from "../models/fitness/weight.model";
import {Profile} from "../models/fitness/profile.model";

@Injectable({providedIn: 'root'})
export class FitnessService {

  constructor(private http: HttpClient) {
  }

  getToken(): Observable<Token> {
    return this.http.get<Token>(environment.fitnessService + '/fitbit/token')
  }

  getWeight(): Observable<Weight[]> {
    return this.http.get<Weight[]>(environment.fitnessService + '/weight')
  }

  getProfile(): Observable<Profile> {
    return this.http.get<Profile>(environment.fitnessService + '/profile')
  }
}
