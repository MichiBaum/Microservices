import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({providedIn: 'root'})
export class FitnessService {

  constructor(private http: HttpClient) {
  }

  getToken(): Observable<String> {
    return this.http.get<String>(environment.fitnessService + '/fitbit/token')
  }

}
