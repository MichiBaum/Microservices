import { Component, inject, OnInit, signal } from '@angular/core';
import { MusicService } from '../../core/api-services/music.service';
import { Artist } from '../../core/models/music/artist.model';
import { ArtistCardComponent } from '../artist-card/artist-card.component';
import { Button } from 'primeng/button';
import { Skeleton } from 'primeng/skeleton';
import { TranslatePipe } from '@ngx-translate/core';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faRotate } from '@fortawesome/free-solid-svg-icons';
import { faSpotify } from '@fortawesome/free-brands-svg-icons';

@Component({
  selector: 'app-music-library-artists',
  imports: [
    ArtistCardComponent,
    Button,
    Skeleton,
    TranslatePipe,
    FaIconComponent
  ],
  templateUrl: './music-library-artists.component.html',
  styleUrl: './music-library-artists.component.scss'
})
export class MusicLibraryArtistsComponent implements OnInit {
  private readonly musicService = inject(MusicService);

  readonly artists = signal<Artist[]>([]);
  readonly loading = signal<boolean>(true);
  readonly syncing = signal<boolean>(false);

  readonly faRotate = faRotate;
  readonly faSpotify = faSpotify;

  ngOnInit(): void {
    this.loadArtists();
  }

  loadArtists(): void {
    this.loading.set(true);
    this.musicService.getLibraryArtists().subscribe({
      next: (data) => {
        this.artists.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  syncWithSpotify(): void {
    this.syncing.set(true);
    this.musicService.syncLibraryArtists().subscribe({
      next: (data) => {
        this.artists.set(data);
        this.syncing.set(false);
      },
      error: () => {
        this.syncing.set(false);
      }
    });
  }
}
