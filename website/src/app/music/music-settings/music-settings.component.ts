import { Component } from '@angular/core';
import {MusicSpotifyLoginComponent} from "../music-spotify-login/music-spotify-login.component";

@Component({
  selector: 'app-music-settings',
    imports: [
        MusicSpotifyLoginComponent
    ],
  templateUrl: './music-settings.component.html',
  styleUrl: './music-settings.component.scss'
})
export class MusicSettingsComponent {

}
