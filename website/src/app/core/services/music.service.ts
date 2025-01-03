import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";
import {Token} from "../models/music/token.model";
import {environment} from "../../../environments/environment";
import {catchError} from "rxjs";

@Injectable({providedIn: 'root'})
export class MusicService{
  private http = inject(HttpClient);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);


  getToken() {
    return this.http.get<Token>(environment.musicService + '/spotify/token')
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }
}
