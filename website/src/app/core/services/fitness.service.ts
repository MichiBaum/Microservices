import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable} from "rxjs";
import {Token} from "../models/fitness/token.model";
import {Weight} from "../models/fitness/weight.model";
import {Profile} from "../models/fitness/profile.model";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";
import {Sleep} from "../models/fitness/sleep.model";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class FitnessService {
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);


  getToken(): Observable<Token> {
    return this.http.get<Token>(this.environment.fitnessService() + '/fitbit/token')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  getWeight(): Observable<Weight[]> {
    return this.http.get<Weight[]>(this.environment.fitnessService() + '/weight')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  getProfile(): Observable<Profile> {
    return this.http.get<Profile>(this.environment.fitnessService() + '/profile')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }

  getSleep(): Observable<Sleep[]> {
    return this.http.get<Sleep[]>(this.environment.fitnessService() + '/sleep')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }
}
