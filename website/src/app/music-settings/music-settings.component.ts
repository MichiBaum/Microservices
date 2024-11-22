import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MusicLoginComponent} from "./music-login/music-login.component";

@Component({
  selector: 'app-music-settings',
  standalone: true,
  imports: [
    MusicLoginComponent
  ],
  templateUrl: './music-settings.component.html',
  styleUrl: './music-settings.component.scss'
})
export class MusicSettingsComponent implements OnInit{

  constructor(
    private readonly headerService: HeaderService,
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music_settings.translationKey)
  }

}
