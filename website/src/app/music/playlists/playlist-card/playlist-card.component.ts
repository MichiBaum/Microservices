import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {Card} from 'primeng/card';
import {Tag} from 'primeng/tag';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faMusic} from '@fortawesome/free-solid-svg-icons';
import {faSpotify} from '@fortawesome/free-brands-svg-icons';
import {RouterLink} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';
import {PrimeTemplate} from 'primeng/api';
import {Playlist} from '../../../core/models/music/playlist.model';

@Component({
  selector: 'app-playlist-card',
  imports: [
    Card,
    Tag,
    FaIconComponent,
    RouterLink,
    TranslatePipe,
    PrimeTemplate
  ],
  templateUrl: './playlist-card.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlaylistCardComponent {
  readonly playlist = input.required<Playlist>();
  readonly faMusic = faMusic;
  readonly faSpotify = faSpotify;
}
