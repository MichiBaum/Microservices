import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {Skeleton} from 'primeng/skeleton';
import {TranslatePipe} from '@ngx-translate/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faPlus, faRotate} from '@fortawesome/free-solid-svg-icons';
import {faSpotify} from '@fortawesome/free-brands-svg-icons';
import {PrimeTemplate} from 'primeng/api';
import {MusicService} from '../../../core/api-services/music.service';
import {UserInfoService} from '../../../core/services/user-info.service';
import {Playlist} from '../../../core/models/music/playlist.model';
import {PlaylistCardComponent} from '../playlist-card/playlist-card.component';
import {PlaylistCreateDialogComponent} from '../playlist-create-dialog/playlist-create-dialog.component';

@Component({
  selector: 'app-music-library-playlists',
  imports: [
    Button,
    Skeleton,
    TranslatePipe,
    FaIconComponent,
    PrimeTemplate,
    PlaylistCardComponent,
    PlaylistCreateDialogComponent
  ],
  templateUrl: './music-library-playlists.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MusicLibraryPlaylistsComponent implements OnInit {
  private readonly musicService = inject(MusicService);
  private readonly userInfoService = inject(UserInfoService);

  readonly playlists = signal<Playlist[]>([]);
  readonly loading = signal<boolean>(true);
  readonly syncing = signal<boolean>(false);
  readonly createDialogOpen = signal<boolean>(false);

  readonly faPlus = faPlus;
  readonly faRotate = faRotate;
  readonly faSpotify = faSpotify;

  ngOnInit(): void {
    this.loadPlaylists();
  }

  loadPlaylists(): void {
    this.loading.set(true);
    this.musicService.getPlaylists().subscribe({
      next: (data) => {
        this.playlists.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.userInfoService.error('Error', 'Failed to load playlists.');
      }
    });
  }

  syncWithSpotify(): void {
    this.syncing.set(true);
    this.musicService.syncPlaylists().subscribe({
      next: (data) => {
        this.playlists.set(data);
        this.syncing.set(false);
        this.userInfoService.info('Sync Complete', 'Playlists synced with Spotify.');
      },
      error: () => {
        this.syncing.set(false);
        this.userInfoService.error('Sync Error', 'Failed to sync playlists with Spotify.');
      }
    });
  }

  openCreateDialog(): void {
    this.createDialogOpen.set(true);
  }

  onPlaylistCreated(playlist: Playlist): void {
    this.playlists.update((current) => [playlist, ...current]);
  }
}
