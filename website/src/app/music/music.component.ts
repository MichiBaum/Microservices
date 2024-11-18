import { Component } from '@angular/core';
import {MusicLoginComponent} from "./music-login/music-login.component";

@Component({
  selector: 'app-music',
  standalone: true,
  imports: [
    MusicLoginComponent
  ],
  templateUrl: './music.component.html',
  styleUrl: './music.component.scss'
})
export class MusicComponent {

}
