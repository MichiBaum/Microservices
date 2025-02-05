import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "../services/user-info.service";
import {Token} from "../models/music/token.model";
import {catchError, Observable} from "rxjs";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class MusicService{
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);


  getToken(): Observable<Token> {
    return this.http.get<Token>(this.environment.musicService() + '/spotify/token')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }
}
