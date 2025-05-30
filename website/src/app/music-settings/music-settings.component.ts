import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MusicLoginComponent} from "./music-login/music-login.component";

@Component({
  selector: 'app-music-settings',
  imports: [
    MusicLoginComponent
  ],
  templateUrl: './music-settings.component.html',
  styleUrl: './music-settings.component.css'
})
export class MusicSettingsComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music_settings.translationKey)
  }

}


