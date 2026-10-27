import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Token} from "../models/music/token.model";
import {Artist} from "../models/music/artist.model";
import {EnvironmentConfig} from "../config/environment.config";
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class MusicService{
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  getToken() {
    return this.http.get<Token>(this.environment.musicService() + '/spotify/token')
  }

  usageAllowed(): Observable<boolean> {
    return this.http.get<boolean>(this.environment.musicService() + '/spotify/usage-allowed')
  }

  getLibraryArtists(): Observable<Artist[]> {
    return this.http.get<Artist[]>(this.environment.musicService() + '/music/library/artists');
  }

  syncLibraryArtists(): Observable<Artist[]> {
    return this.http.post<Artist[]>(this.environment.musicService() + '/music/library/artists/sync', {});
  }

}
