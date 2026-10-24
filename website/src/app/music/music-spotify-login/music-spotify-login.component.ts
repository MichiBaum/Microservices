import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Button } from "primeng/button";
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faSpotify } from '@fortawesome/free-brands-svg-icons';
import { TranslatePipe } from '@ngx-translate/core';
import { RouterNavigationService } from "../../core/services/router-navigation.service";
import { MusicService } from "../../core/api-services/music.service";

@Component({
  selector: 'app-music-spotify-login',
  imports: [
    Button,
    FaIconComponent,
    TranslatePipe
  ],
  templateUrl: './music-spotify-login.component.html',
  styleUrl: './music-spotify-login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MusicSpotifyLoginComponent {
  private readonly musicService = inject(MusicService);
  private readonly router = inject(RouterNavigationService);

  protected readonly faSpotify = faSpotify;

  getTokenUrl(){
    this.musicService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

}


