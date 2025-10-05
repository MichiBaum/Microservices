import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Token} from "../models/fitness/token.model";
import {Weight} from "../models/fitness/weight.model";
import {Profile} from "../models/fitness/profile.model";
import {Sleep, SleepStage} from "../models/fitness/sleep.model";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class FitnessService {
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);


  getToken(): Observable<Token> {
    return this.http.get<Token>(this.environment.fitnessService() + '/fitbit/token')
  }

  getWeight(): Observable<Weight[]> {
    return this.http.get<Weight[]>(this.environment.fitnessService() + '/weight')
  }

  getProfile(): Observable<Profile> {
    return this.http.get<Profile>(this.environment.fitnessService() + '/profile')
  }

  getSleep(): Observable<Sleep[]> {
    return this.http.get<Sleep[]>(this.environment.fitnessService() + '/sleeps')
  }

    getSleepStages(sleepId: string) {
        return this.http.get<SleepStage[]>(this.environment.fitnessService() + '/sleeps/' + sleepId + '/stages')
    }
}
