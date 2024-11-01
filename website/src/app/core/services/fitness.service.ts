import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {FitnessTokenModel} from "../models/fitness-token.model";

@Injectable({providedIn: 'root'})
export class FitnessService {

  constructor(private http: HttpClient) {
  }

  getToken(): Observable<FitnessTokenModel> {
    return this.http.get<FitnessTokenModel>(environment.fitnessService + '/fitbit/token')
  }

}
