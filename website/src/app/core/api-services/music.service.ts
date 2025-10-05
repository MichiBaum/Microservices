import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Token} from "../models/music/token.model";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class MusicService{
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  getToken() {
    return this.http.get<Token>(this.environment.musicService() + '/spotify/token')
  }
}
