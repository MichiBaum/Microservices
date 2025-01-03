import {Component, inject} from '@angular/core';
import {Button} from "primeng/button";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {MusicService} from "../../core/services/music.service";

@Component({
  selector: 'app-music-login',
  imports: [
    Button
  ],
  templateUrl: './music-login.component.html',
  styleUrl: './music-login.component.scss'
})
export class MusicLoginComponent {
  private readonly musicService = inject(MusicService);
  private readonly router = inject(RouterNavigationService);

  getTokenUrl(){
    this.musicService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

}
