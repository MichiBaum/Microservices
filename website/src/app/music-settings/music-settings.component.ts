import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MusicSpotifyLoginComponent} from "./music-spotify-login/music-spotify-login.component";

@Component({
  selector: 'app-music-settings',
  imports: [
    MusicSpotifyLoginComponent
  ],
  templateUrl: './music-settings.component.html',
  styleUrl: './music-settings.component.scss'
})
export class MusicSettingsComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music_settings.translationKey)
  }

}
