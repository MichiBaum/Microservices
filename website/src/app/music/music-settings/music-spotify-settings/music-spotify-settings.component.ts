import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Card } from 'primeng/card';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faSpotify } from '@fortawesome/free-brands-svg-icons';
import { TranslatePipe } from '@ngx-translate/core';
import { MusicSpotifyLoginComponent } from '../../music-spotify-login/music-spotify-login.component';

@Component({
  selector: 'app-music-spotify-settings',
  standalone: true,
  imports: [
    Card,
    FaIconComponent,
    TranslatePipe,
    MusicSpotifyLoginComponent
  ],
  templateUrl: './music-spotify-settings.component.html',
  styleUrl: './music-spotify-settings.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MusicSpotifySettingsComponent {
  protected readonly faSpotify = faSpotify;
}
