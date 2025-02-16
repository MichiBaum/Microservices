import {Component, inject} from '@angular/core';
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {MusicService} from "../../core/api-services/music.service";

@Component({
  selector: 'app-music-spotify-login',
  imports: [
    Button
  ],
  templateUrl: './music-spotify-login.component.html',
  styleUrl: './music-spotify-login.component.css'
})
export class MusicSpotifyLoginComponent {
  private readonly musicService = inject(MusicService);
  private readonly router = inject(RouterNavigationService);

  getTokenUrl(){
    this.musicService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

}


