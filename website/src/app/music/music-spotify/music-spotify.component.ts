import { Component } from '@angular/core';
import {MusicSpotifyLoginComponent} from "../../music-settings/music-spotify-login/music-spotify-login.component";

@Component({
  selector: 'app-music-spotify',
    imports: [
        MusicSpotifyLoginComponent
    ],
  templateUrl: './music-spotify.component.html',
  styleUrl: './music-spotify.component.scss'
})
export class MusicSpotifyComponent {

}
