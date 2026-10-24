import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { HeaderService } from "../../core/services/header.service";
import { Sides } from "../../core/config/sides";
import { MusicSpotifySettingsComponent } from "./music-spotify-settings/music-spotify-settings.component";

@Component({
  selector: 'app-music-settings',
  standalone: true,
  imports: [
    MusicSpotifySettingsComponent
  ],
  templateUrl: './music-settings.component.html',
  styleUrl: './music-settings.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MusicSettingsComponent implements OnInit {
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music_settings.translationKey);
  }
}
